package com.biu.modulebase.common.util;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 防止Context泄漏的通用Handler抽象类
 * 
 * @param <T>
 *            易造成内存泄露的对象的引用
 */
public abstract class WeakReferenceHandler<T> extends Handler {

	private WeakReference<T> mReference;

	public WeakReferenceHandler(T reference) {
		mReference = new WeakReference<T>(reference);
	}

	@Override
	public void handleMessage(Message msg) {
		if (mReference.get() == null)
			return;
		handleMessage(mReference.get(), msg);
	}

	protected abstract void handleMessage(T reference, Message msg);
}
