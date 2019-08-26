package com.example.todolist.Services

import com.example.todolist.Model.CategoryItem
import com.example.todolist.Model.ListItem
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*
import kotlin.collections.ArrayList

object FirebaseService {

    var userID = ""

    init {
    }

    fun loadCategories(completion: (MutableList<CategoryItem>) -> Unit) {
        val collectionRef =
            FirebaseFirestore.getInstance().collection("/Users/${userID}/Categories")
        val categories = mutableListOf<CategoryItem>()
        collectionRef.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                querySnapshots: QuerySnapshot?,
                firebaseFirestoreException: FirebaseFirestoreException?
            ) {
                categories.clear()
                for (snapshot in querySnapshots!!) {
                    val item = snapshot.toObject(CategoryItem::class.java)
                    categories.add(item)
                }
                if (categories.isEmpty()) {
                    createSampleData()
                } else {
                    completion(categories)
                }
            }
        })
    }

    fun createCategory(names: ArrayList<String>, completion: (Boolean) -> Unit) {
        val collectionRef =
            FirebaseFirestore.getInstance().collection("/Users/${userID}/Categories")
        var categoryItems= names.map {
            CategoryItem(it, "", 0, 0)
        } as ArrayList<CategoryItem>

        val batch = FirebaseFirestore.getInstance().batch()
        for (item in categoryItems) {
            val ref = collectionRef.document(item.title.toLowerCase())
            batch.set(ref, item)
        }
        batch.commit().addOnCompleteListener {
            completion(true)
        }.addOnFailureListener {
            completion(false)
        }
    }

    private fun createSampleData() {
        createCategory(arrayListOf<String>("Work", "School", "Home")) {}
    }

    fun loadItems(categoryName: String, completion: (MutableList<ListItem>) -> Unit) {
        val collectionItemsRef = FirebaseFirestore.getInstance()
            .collection("/Users/${userID}/Categories/${categoryName}/items")
        val items = mutableListOf<ListItem>()

        collectionItemsRef.orderBy("date", Query.Direction.ASCENDING).get().addOnSuccessListener {
            items.clear()
            for (snapshot in it) {
                val item = snapshot.toObject(ListItem::class.java)
                item.id = snapshot.id
                items.add(item)
            }
            completion(items)
        }.addOnFailureListener {
            completion(mutableListOf())
        }
    }

    fun itemCompletionSwitch(categoryName: String, id: String, value: Boolean) {
        val categoryRef =
            FirebaseFirestore.getInstance().document("/Users/${userID}/Categories/${categoryName}")
        val itemRef = categoryRef.collection("items").document(id)
        if (value)
            categoryRef.update("done", FieldValue.increment(1))
        else
            categoryRef.update("done", FieldValue.increment(-1))
        val map = hashMapOf<String, Boolean>()
        map.put("checked", value)
        itemRef.set(map, SetOptions.merge())


    }

    fun createItem(categoryName: String, item: ListItem, completion: (Boolean) -> Unit) {
        val categoryDocumentRef =
            FirebaseFirestore.getInstance().document("Users/${userID}/Categories/${categoryName}")
        categoryDocumentRef.update("tasks", FieldValue.increment(1))
        val itemCollectionRef = categoryDocumentRef.collection("items")
        itemCollectionRef.document().set(item).addOnSuccessListener {
            completion(true)
        }.addOnFailureListener {
            completion(false)
        }
    }

    fun updateItem(categoryName: String, item: ListItem, completion: (Boolean) -> Unit) {
        val path = "/Users/${userID}/Categories/${categoryName}/items/${item.id}"
        val itemRef = FirebaseFirestore.getInstance().document(path)
        val changes = hashMapOf<String, Any>()
        changes["checked"] = item.checked
        changes["subject"] = item.subject
        changes["date"] = item.date
        changes["text"] = item.text
        itemRef.update(changes).addOnSuccessListener {
            completion(true)
        }.addOnFailureListener {
            completion(false)
        }
    }

    fun deleteItem(id: String, categoryName: String, completion: (Boolean) -> Unit) {
        val categoryRef =
            FirebaseFirestore.getInstance().document("Users/${userID}/Categories/${categoryName}")
        val itemRef = categoryRef.collection("items").document(id)
        itemRef.delete().addOnSuccessListener {
            categoryRef.update("tasks", FieldValue.increment(-1))
            categoryRef.update("done", FieldValue.increment(-1))
            completion(true)
        }.addOnFailureListener {
            completion(false)
        }
    }

}
