package uz.mymax.million.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import uz.mymax.million.PlaylistViewModel
import uz.mymax.million.adapter.PlaylistAdapter
import uz.mymax.million.player.PlayerViewModel

val appModule = module {

    factory { PlaylistAdapter() }

    viewModel { PlaylistViewModel() }

    viewModel { PlayerViewModel() }
}