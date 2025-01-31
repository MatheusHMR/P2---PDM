package pdm.compose.prova2_pdm.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import pdm.compose.prova2_pdm.model.Bike
import pdm.compose.prova2_pdm.model.Cliente


class BikeRepository(
    private val bikeCollection: CollectionReference
) {
    suspend fun addBike(bike: Bike) {
        try {
            val newDocRef = bikeCollection.document()
            val bikeWithId = bike.copy(codigo = newDocRef.id)
            newDocRef.set(bikeWithId).await()
        } catch (e: Exception){
            Log.e("BikeRepository", "Error inside BikeRepository - addBike")
        }
    }

    suspend fun getAll() : List<Bike> {
        return try {
            bikeCollection.get().addOnSuccessListener {
                Log.d("BikeRepository", "Bikes acquired: $it")
            }.await().documents
                .mapNotNull { it.toObject(Bike::class.java) }
        } catch (e: Exception){
            Log.e("BikeRepository", "Error inside BikeRepository - getAllBikes")
            emptyList()
        }
    }

    suspend fun getBikesByClienteId(clienteId: String) : List<Bike> {
       return try {
           bikeCollection.whereEqualTo("clienteId", clienteId).get().await()
               .documents.mapNotNull { it.toObject(Bike::class.java) }
        } catch (e: Exception){
            Log.e("BikeRepository", "Error inside BikeRepository - getBikesByClienteId")
           emptyList()
        }
    }

    suspend fun update(bike: Bike) {
        try {
            bikeCollection.document(bike.codigo).set(bike)
                .addOnSuccessListener {
                    Log.d("BikeRepository", "Updated bike: $bike successfully")
                }.await()
        } catch (e: Exception){
            Log.e("BikeRepository", "Error inside BikeRepository - updateBike")
        }
    }

    suspend fun delete(codigoBike: String) {
        try {
            bikeCollection.document(codigoBike).delete().addOnSuccessListener {
                Log.d("BikeRepository", "Deleted bike: $codigoBike")
            }.await()
        } catch (e: Exception) {
            Log.e("BikeRepository", "Error inside BikeRepository - deleteBike")
        }
    }

    suspend fun deleteBikesByClienteId(clienteId: String): Boolean {
        return try {
            val bikesToDelete = getBikesByClienteId(clienteId)
            for (bike in bikesToDelete){
                delete(bike.codigo)
            }
            Log.d("BikeRepository", "Bikes to delete: $bikesToDelete")
            true
        } catch (e: Exception) {
            Log.e("BikeRepository", "Error inside BikeRepository - deleteBikesByClienteId", e)
            false
        }
    }
}