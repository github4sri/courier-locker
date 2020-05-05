package name.lmj0011.courierlocker.helpers.interfaces

import android.app.Activity
import android.view.View
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.app_bar_main.*
import name.lmj0011.courierlocker.MainActivity
import name.lmj0011.courierlocker.helpers.ListLock

interface SearchableRecyclerView {

    fun toggleSearch(mainActivity: MainActivity, searchView: SearchView?, giveFocus: Boolean) {
        searchView?.let {
            if(giveFocus) {
                it.visibility = View.VISIBLE
                mainActivity.toolbar.visibility = View.GONE

                // dumb hack https://stackoverflow.com/a/47287337/2445763
                it.isIconified = false
                mainActivity.showKeyBoard(it)
            } else {
                it.clearFocus()
                it.visibility = View.GONE
                mainActivity.toolbar.visibility = View.VISIBLE
                mainActivity.hideKeyBoard(it)
            }
        }
    }
}