package ru.ivos.notepad.db

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import ru.ivos.notepad.EditActivity
import ru.ivos.notepad.IntentConsts
import ru.ivos.notepad.R


class NoteAdapter(listMine: ArrayList<ListItem>, contextMain: Context) : RecyclerView.Adapter<NoteAdapter.NoteHolder>() {

    var listItems = listMine
    var context = contextMain

    class NoteHolder(itemView: View, contextHolder: Context) : RecyclerView.ViewHolder(itemView){

        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val context = contextHolder

        fun setData(item: ListItem){
            tvTitle.text = item.title
            itemView.setOnClickListener {
                val intent = Intent(context, EditActivity::class.java).apply {
                    putExtra(IntentConsts.INTENT_TITLE_KEY, item.title)
                    putExtra(IntentConsts.INTENT_CONTENT_KEY, item.content)
                    putExtra(IntentConsts.INTENT_URL_KEY, item.url)
                    putExtra(IntentConsts.INTENT_ID_KEY, item.id)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {

        val inflater = LayoutInflater.from(parent.context)
        return NoteHolder(inflater.inflate(R.layout.rc_item, parent, false), context)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {

        holder.setData(listItems[position])
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(listItem: List<ListItem>){
        listItems.clear()
        listItems.addAll(listItem)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int, dbManager: DBManager){
        dbManager.removeItemFromDb(listItems[position].id.toString())
        listItems.removeAt(position)
        notifyItemRangeChanged(0, listItems.size)
        notifyItemRemoved(position)
    }
}