package kr.ac.kpu.ecobasket

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.activity_qractivity.*
import kotlinx.android.synthetic.main.top_action_bar_in_qr.*
import org.jetbrains.anko.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 바코드 생성기 링크
 * https://wepplication.github.io/tools/barcodeGen/
 */

// QR Debug용 리스너 : Alias변환
typealias QRListener = (QR : String) -> Unit

class QRActivity : AppCompatActivity() {

    private var QRCode : String = ""    //QR코드
    private var inputCode : EditText? = null        //직접 입력 코드
    private var cameraController : CameraControl? = null    //카메라 장치 제어(플래시 목적)
    private var cameraInfo : CameraInfo? = null //카메라 정보 불러오기

    private lateinit var cameraExecutor: ExecutorService    //카메라 서비스 실행 인스턴스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qractivity)

        btn_close_qr.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        btn_inputCode.setOnClickListener {
            /* 바코드 직접 입력 dialog 생성 */
            alert {
                customView{
                    inputCode = editText {
                        hint = "코드를 직접 입력하세요"
                    }
                }
                yesButton {
                    QRCode = inputCode?.text.toString()
                    setResult(Activity.RESULT_OK, intent)
                    intent.putExtra("qr", QRCode)
                    finish()
                }
                noButton { toast("코드 입력 취소") }
            }.show()

        }

        btn_flash.setOnClickListener {
            btn_flash?.let {
                it.isSelected = !it.isSelected

                // 플래시 on/off
                when (cameraInfo?.torchState?.value) {
                    TorchState.ON -> {
                        cameraController?.enableTorch(false)
                        Log.i(TAG, "Flash Off")
                    }
                    TorchState.OFF -> {
                        cameraController?.enableTorch(true)
                        Log.i(TAG, "Flash On")
                    }
                }
            }
        }

        // 카메라 접근권한 요청
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    /* 뷰파인더(카메라 화면에서 미리보기) 구현 : 인스턴스 만들기 *
     *
     * 결과는 CameraX LifeCycle에 바인딩 됨
     */
    private fun startCamera() {
        // 카메라 LifeCycle 바인딩을 위한 인스턴스
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)  //인스턴스 Get

        cameraProviderFuture.addListener(Runnable {
            // CameraX 라이프사이클 바인딩
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview : CameraX의 뷰파인더 구현 클래스
            // Preview 객체 빌드
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            // ImageAnalyzer : CameraX의 이미지 분석기 클래스
            // ImageAnalyzer 객체 빌드
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                /** STRATEGY_KEEP_ONLY_LATEST : 비차단모드 = analyze()호출 시점 마지막 사용가능 프레임만 수신
                 * 동시성 프레임으로 인한 다중 분석으로 원활한 인식이 불가할 수 있음
                 * cf) 이미지 분석 기본설정 : STRATEGY_BLOCK_PRODUCER (차단모드)
                 */
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QRCodeAnalyzer { QRCode ->   //인식할시 대여/반납
                        this@QRActivity.QRCode = QRCode
                        Log.d(TAG, "$QRCode")
                        setResult(Activity.RESULT_OK, intent)
                        intent.putExtra("qr", QRCode)
                        finish()
                    })
                }

            // 후방 카메라로 기본 설정
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Rebinding 전에 Unbind
                cameraProvider.unbindAll()

                // 카메라 Bind하고 LifeCycle에 넘기기
                val camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer)

                //Controller, Info 객체 정의
                cameraController = camera!!.cameraControl
                cameraInfo = camera!!.cameraInfo

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
        //GetMainExecutor : Main Thread에 작동
    }

    //접근권한 수락 확인 함수
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /* 출력 디렉토리 Get함수
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }*/

    override fun onDestroy() {
        super.onDestroy()
        cameraController?.enableTorch(false)
        cameraExecutor.shutdown()
    }

    //접근권한 반환값에 따른 결과
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    //기본 상수 Base Set
    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}

//QRCode 인식 분석 Inner Class
private class QRCodeAnalyzer(private val listener: QRListener) : ImageAnalysis.Analyzer {

    //CameraX 분석 코드 : 화면 인식
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            //바코드 인식 후 정보 전달 함수
            scanBarcodes(image, listener )
        }
        //imageProxy 닫기
        imageProxy.close()
    }
}

//QR옵션 지정 및 QR코드 해석 함수
private fun scanBarcodes(image: InputImage, listener: QRListener) {

    //스캐너 옵션 설정
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE)
        //기타 코드를 추가할 수 있음 - 필요하면 추가함(옵션없애서 전체 포멧 스캔도 가능)
        .build()
    //바코드 스캐너 인스턴스 Get
    val scanner = BarcodeScanning.getClient(options)

    //바코드 정보 넘기기
    val result = scanner.process(image)
        .addOnSuccessListener { barcodes ->
            for (barcode in barcodes) {
                listener(barcode.rawValue ?: "")
                //graphicOverlay.add(QRGraphic(graphicOverlay, barcode))
            }
        }
        .addOnFailureListener { }
        .addOnCompleteListener { }
}

