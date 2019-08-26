package layout

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.todolist.Activity.CategoryActivity
import com.example.todolist.Activity.MainActivity
import com.example.todolist.Model.CategoryItem
import com.example.todolist.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import android.util.Pair as UtilPair

class CategoryItemAdapter(categories : MutableList<CategoryItem>?, context : Context) : PagerAdapter() {
    private val context : Context
    private var categories : List<CategoryItem>? = null
    private var inflater : LayoutInflater? = null
    private var doneCnt = 0L
    private var tasksCnt =0L

    init {
        this.context = context
        this.categories = categories
        inflater = LayoutInflater.from(context)
    }

    override fun isViewFromObject(view: View, obj: Any) = view == obj

    override fun getCount() = categories!!.size

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = inflater?.inflate(R.layout.category_item,container,false)

        val imageView = view?.findViewById(R.id.cv_avatar_image) as CircleImageView
        val titleView = view?.findViewById(R.id.tv_category_name) as TextView
        val tasksView = view?.findViewById(R.id.tv_remaining_tasks_count) as TextView
        val progressBar = view?.findViewById(R.id.progress_bar) as ProgressBar
        val percentageView = view?.findViewById(R.id.tv_percentage) as TextView

        val done  = categories?.get(position)?.done!!
        val cnt = categories?.get(position)?.tasks!!
        val title = categories?.get(position)?.title!!.capitalize()

        doneCnt += done
        tasksCnt += cnt

        if(cnt == 0L) {
            percentageView.setText("100%")
            progressBar.progress = 100
        }else{
            progressBar.progress = (done*100/cnt).toInt()
            percentageView.setText("${done*100L/cnt}%")
        }
        titleView.text = title
        tasksView.setText("${done}/${cnt}")
        val imageIdentifier = context.resources.getIdentifier(
            title.toLowerCase(),
            "drawable",
            context.getPackageName())
        if(imageIdentifier !=0){
            imageView.setImageResource(imageIdentifier)
        }

        view.setOnClickListener{
            val intent = Intent(context,CategoryActivity::class.java)
                .putExtra("title",titleView.text.toString())
                .putExtra("done",done)
                .putExtra("tasks",cnt)
                .putExtra("imageIdentifier",imageIdentifier)
            val options = ActivityOptions.makeSceneTransitionAnimation(context as Activity,
                UtilPair.create(imageView,context.resources.getString(R.string.transition_category_image)),
                UtilPair(titleView,context.resources.getString(R.string.transition_category_title)),
                UtilPair(tasksView,context.resources.getString(R.string.transition_category_taskcount)),
                UtilPair(progressBar,context.resources.getString(R.string.transition_category_progress_bar)),
                UtilPair(percentageView,context.resources.getString(R.string.transition_category_percentage)))
            context.startActivityForResult(intent,MainActivity.REQUEST_FOR_ACTIVITY_CODE,options.toBundle())
        }
        container.addView(view)
        return view
    }
    fun getCounts()=Pair(doneCnt,tasksCnt)
}