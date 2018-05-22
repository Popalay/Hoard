package com.github.popalay.store.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.popalay.store.R
import com.github.popalay.store.ServiceLocator
import com.github.popalay.store.store.GithubUserStore
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_users_list.*
import kotlin.properties.Delegates

class UsersListActivity : AppCompatActivity() {

    private var adapter: UserAdapter by Delegates.notNull()
    private var disposable = Disposables.disposed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_list)
        initViews()
        loadUsers()
    }

    private fun initViews() {
        adapter = UserAdapter()
        listUsers.adapter = adapter
        refreshLayout.setOnRefreshListener {
            loadUsers()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun loadUsers() {
        disposable.dispose()
        disposable = ServiceLocator.githubUserStore.get(GithubUserStore.Key.All)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                adapter.submitList(it)
                refreshLayout.isRefreshing = false
            }, { Log.d("UsersListActivity", "Load users error ${it.localizedMessage}") })
    }
}