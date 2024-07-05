package it.polito.teamhub.ui.view.component.statistics

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import it.polito.teamhub.R

@SuppressLint("ViewConstructor")
class CustomMarkerView(
    context: Context,
    layoutResource: Int,
    private var member: Boolean? = false
) :
    MarkerView(context, layoutResource) {
    private val textView: TextView = findViewById(R.id.marker_text)

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        val pieEntry = entry as PieEntry
        if (pieEntry.value.toInt() == 1) {
            if (member == true) {
                "${pieEntry.label}: ${pieEntry.value.toInt()} member".also { textView.text = it }
            } else {
                "${pieEntry.label}: ${pieEntry.value.toInt()} task".also { textView.text = it }
            }
        } else {
            if (member == true) {
                "${pieEntry.label}: ${pieEntry.value.toInt()} members".also { textView.text = it }
            } else {
                "${pieEntry.label}: ${pieEntry.value.toInt()} tasks".also { textView.text = it }
            }
        }
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat() + 20)
    }
}