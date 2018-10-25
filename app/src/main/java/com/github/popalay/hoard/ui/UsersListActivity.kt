package com.github.popalay.hoard.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.popalay.hoard.R
import com.github.popalay.hoard.ServiceLocator
import com.github.popalay.hoard.hoard.GithubUserHoard
import com.github.popalay.hoard.hoard.Result
import com.github.popalay.hoard.hoard.getWithResult
import com.github.popalay.hoard.utils.Identifiable
import com.github.popalay.hoard.utils.ResultDelegates
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_users_list.*
import kotlin.properties.Delegates

class UsersListActivity : AppCompatActivity(), ListStatesView {

    private var adapter: UserAdapter by Delegates.notNull()
    private var disposable = Disposables.disposed()
    private var result: Result<List<Identifiable>> by ResultDelegates.list()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)
        initViews()
        loadUsers()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    override fun showRefresh() {
        supportActionBar?.subtitle = "Loading..."
        refreshLayout.isRefreshing = true
    }

    override fun hideRefresh() {
        supportActionBar?.subtitle = null
        refreshLayout.isRefreshing = false
    }

    override fun showOutdatedState() {
        supportActionBar?.subtitle = "Outdated"
    }

    override fun showEmptyState() {
        supportActionBar?.subtitle = "Empty"
    }

    override fun showErrorState() {
        supportActionBar?.subtitle = "Error"
    }

    override fun showContent(content: List<Identifiable>) {
        adapter.submitList(content)
    }

    private fun initViews() {
        setSupportActionBar(toolbar)
        adapter = UserAdapter()
        listUsers.adapter = adapter
        refreshLayout.setOnRefreshListener {
            loadUsers()
        }
    }

    private fun loadUsers() {
        disposable.dispose()
        disposable = ServiceLocator.githubUserStore.getWithResult(
            GithubUserHoard.Key.All,
            dataIsEmpty = { it.isEmpty() })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result = it },
                { Log.d("UsersListActivity", "Load users error ${it.localizedMessage}") })
    }
}