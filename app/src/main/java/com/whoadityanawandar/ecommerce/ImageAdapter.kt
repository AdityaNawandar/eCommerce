package com.whoadityanawandar.ecommerce

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ImageAdapter(
    private val context: Context,
    private val numbersInWords: Array<String>,
    private val numberImage: IntArray
) :
    BaseAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var ivCategoryImage: ImageView
    private lateinit var tvCategoryName: TextView
    override fun getCount(): Int {
        return numbersInWords.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        var convertView = convertView
        if (layoutInflater == null) {
            layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }
        if (convertView == null) {
            convertView = layoutInflater!!.inflate(R.layout.category_item, null)
        }
        ivCategoryImage = convertView!!.findViewById(R.id.imgvwCategory)
        tvCategoryName = convertView.findViewById(R.id.txtvwCategoryName)
        ivCategoryImage.setImageResource(numberImage[position])
        tvCategoryName.text = numbersInWords[position]
        return convertView
    }
}