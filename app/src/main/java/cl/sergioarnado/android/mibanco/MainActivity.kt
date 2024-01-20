package cl.sergioarnado.android.mibanco


import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.sergioarnado.android.mibanco.data.Clientes
import cl.sergioarnado.android.mibanco.ui.ListaClientesViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.camera.view.PreviewView
import androidx.camera.view.PreviewView.ScaleType
import androidx.compose.foundation.Image
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices


class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){}
    private lateinit var cameraExecutor: Executor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        cameraExecutor = Executors.newSingleThreadExecutor()
        setContent {
            AppBankUI()
            UbicacionUI()

            val imageCapture = remember{
                ImageCapture.Builder().build()

            }
            Box(modifier = Modifier.fillMaxSize()){
                CameraPreview(modifier = Modifier.fillMaxSize(),imageCapture = imageCapture)
                IconButton(onClick = {capturarfoto(imageCapture)}) {
                    Image(painter = painterResource(id = R.drawable.dec), contentDescription = "",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .size(300.dp)
                    )

                }
            }


        }
    }

    private fun capturarfoto(imageCapture: ImageCapture) {
        val file = File.createTempFile("img", ".jpg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(outputFileOptions, cameraExecutor, object : ImageCapture.OnImageCapturedCallback(),
            ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                println("el Uri es ${outputFileResults.savedUri}")
            }
        })
    }
}


@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    scaleType: ImageView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    imageCapture: ImageCapture

) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    AndroidView(factory = { context ->
        val previewView = PreviewView(context).apply {

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            this.scaleType = scaleType}


        val previewUseCase = Preview.Builder().build()
        previewUseCase.setSurfaceProvider(previewView.surfaceProvider)

        coroutineScope.launch{
            val cameraProvider = context.cameraProvider()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, previewUseCase, imageCapture)
        }




        previewView



    })
}


suspend fun Context.cameraProvider() : ProcessCameraProvider = suspendCoroutine{ continuation ->


    val listenableFuture = ProcessCameraProvider.getInstance(this)
    listenableFuture.addListener({

        continuation.resume(listenableFuture.get())

    }, ContextCompat.getMainExecutor(this))

}


@Preview(showSystemUi = true)
@Composable
fun FormIngresoUI() {
    val (usuario, setUsuario) = remember { mutableStateOf("") }
    val (contrasena, setContrasena) = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.padding(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "IplaBank",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                value = usuario,
                onValueChange = { setUsuario(it) },
                label = { Text("Usuario") }
            )
            TextField(
                value = contrasena,
                onValueChange = { setContrasena(it) },
                label = { Text("Contrasena") }
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Primer botón
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Ingresar")
            }

            // Separador
            Spacer(modifier = Modifier.height(16.dp))

            // Segundo botón
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Solicitar Cuenta")
            }
        }
    }
}

@Composable
fun FormCuenta(vmListaCliente: ListaClientesViewModel = viewModel(factory = ListaClientesViewModel.Factory)) {
    var nombreCompleto by remember { mutableStateOf<String>("") }
    var rut by remember { mutableStateOf<String>("") }
    var fechaNacimiento by remember { mutableStateOf<String>("") }
    var email by remember { mutableStateOf<String>("") }
    var telefono by remember { mutableStateOf<String>("") }
    var imagenCedulaFrontal by remember { mutableStateOf<String>("") }
    var imagenCedulaTrasera by remember { mutableStateOf<String>("") }
    var latitud by remember { mutableStateOf<Double>(0.0) }
    var longitud by remember { mutableStateOf<Double>(0.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = nombreCompleto,
            onValueChange = { nombreCompleto = it },
            label = { Text("Nombre Completo") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = rut,
            onValueChange = { rut = it },
            label = { Text("RUT") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = { Text("Fecha de Nacimiento") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = imagenCedulaFrontal,
            onValueChange = { imagenCedulaFrontal = it },
            label = { Text("Imagen de la cédula frontal") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = imagenCedulaTrasera,
            onValueChange = { imagenCedulaTrasera = it },
            label = { Text("Imagen de la cédula trasera") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val clientes = Clientes (
                null,
                nombreCompleto, rut, 0, email, telefono, 0.0, 0.0, "", "", 0 )

            vmListaCliente.ingresarSolicitud(clientes)
        }) {
            Text(text = "Enviar Solicitud")
        }
    }
}

@Composable
fun AppBankUI(
    vmListaCliente: ListaClientesViewModel = viewModel(factory = ListaClientesViewModel.Factory)
) {
    // se ejecuta al iniciar el compose
    LaunchedEffect(Unit) {

    }

    LazyColumn {
        items(vmListaCliente.clientes) {
            Text(it.nombreCompleto)
        }

    }

}

@Composable
fun UbicacionUI(){
    val contexto = LocalContext.current
    var mensaje by rememberSaveable { mutableStateOf("ubicacion: ") }
    val lanzadorPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            if ( it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false)
                ||
                it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false)
            ) {

                val locationServices = LocationServices.getFusedLocationProviderClient(contexto)
                UbicacionRepositorio(locationServices)
                val repositorio = UbicacionRepositorio(



                // permisos ok, recuperar ubicación
            } else {
                // mostrar mensaje error,
                // explicación permisos requeridos
                mensaje = "otorgar permisos"
            }
        }
    )
    Column {
        Text(mensaje)
        Button(onClick = {

        }) {
            Text("Ubicacion")
            
        }
    }
}

@Composable
fun AppCamara(){
    val lanzadorDeFotos = rememberLauncherForActivityResult(
        contract = ActivityResultContract.(),
        onResult = )

    Column {
        Button(onClick = {

        }) {
            Text("Capturar Foto")

        }
    }
}