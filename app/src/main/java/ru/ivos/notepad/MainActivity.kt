package ru.ivos.notepad

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.ivos.notepad.databinding.ActivityMainBinding
import ru.ivos.notepad.db.DBManager
import ru.ivos.notepad.db.NoteAdapter


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val dbManager = DBManager(this)
    val noteAdapter = NoteAdapter(ArrayList(), this)
    var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initSearchView()
    }

    override fun onResume() {
        super.onResume()
        dbManager.openDb()
        fillAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        dbManager.closeDb()
    }

    fun onClickAdd(view: View) {
        val intent = Intent(this, EditActivity::class.java)
        startActivity(intent)
    }

    fun initSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                fillAdapter(newText!!)
                return true
            }
        })
    }

    fun init() {
        binding.rcView.layoutManager = LinearLayoutManager(this)
        val swapManager = getSwapManager()
        swapManager.attachToRecyclerView(binding.rcView)
        binding.rcView.adapter = noteAdapter
    }

    fun fillAdapter(text: String) {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Main).launch {
            val list = dbManager.readDBData(text)
            noteAdapter.updateAdapter(list)
            if (list.size > 0) {
                binding.noElements.visibility = View.GONE
            } else {
                binding.noElements.visibility = View.VISIBLE
            }
        }
    }

    fun getSwapManager(): ItemTouchHelper {
        return ItemTouchHelper(object : ItemTouchHelper.
        SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                noteAdapter.removeItem(viewHolder.adapterPosition, dbManager)
            }
        })
    }


}
