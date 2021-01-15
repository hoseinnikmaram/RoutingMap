package com.nikmaram.map.Network;

import io.reactivex.disposables.CompositeDisposable;

public final class SingleCom {
    public static  CompositeDisposable INSTANCE;
    public static final CompositeDisposable com(){
        if(INSTANCE==null){
            INSTANCE=new CompositeDisposable();
        }
        return INSTANCE;
    }

}
