package com.zileanstdio.chatapp.DI.main;

import androidx.lifecycle.ViewModel;

import com.zileanstdio.chatapp.DI.ViewModelKey;
import com.zileanstdio.chatapp.Ui.main.MainViewModel;
import com.zileanstdio.chatapp.Ui.main.connections.chat.ChatViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    public abstract ViewModel bindMainViewModel(MainViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ChatViewModel.class)
    public abstract ViewModel bindChatViewModel(ChatViewModel viewModel);
}
