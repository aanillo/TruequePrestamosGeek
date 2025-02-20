package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import entity.Libro
import entity.Producto
import entity.Videojuego
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import view.ProductoViewModel


class Ui{
    @Composable
    fun MyApp() {
        val viewModel = remember { ProductoViewModel() }
        val productos = viewModel.productos
        val errorMensaje = viewModel.errorMessage

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF9A0007))
                .padding(16.dp)
        ) {
            Text(
                text = "BIENVENIDO/A a la APP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

            Spacer(modifier = Modifier.height(16.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Seleccione una opción para cargar los productos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { viewModel.cargarProductosDesdeTXT("src/main/resources/ficheros/mis_productos.txt") },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB71C1C))
                ) {
                    Text("Cargar desde TXT", color = Color.White)
                }
                Button(
                    onClick = { viewModel.cargarProductosDesdeJson("src/main/resources/ficheros/mis_productos.json") },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB71C1C))
                ) {
                    Text("Cargar desde JSON", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            if (errorMensaje.isNotEmpty()) {
                Text("Error: $errorMensaje", color = Color.Red)
            }


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                items(productos) { producto ->
                    ProductoItem(producto)
                }
            }
        }
    }

    @Composable
    fun ProductoItem(producto: Producto) {
        Card(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Título: ${producto.titulo}", fontWeight = FontWeight.Bold)
                Text("Descripción: ${producto.descripcion}")
                Text("Estado: ${producto.estado}")
                if (producto is Videojuego) {
                    Text("Plataforma: ${producto.plataforma}")
                } else if (producto is Libro) {
                    Text("Autor: ${producto.autor}")
                }
            }
        }
    }
}