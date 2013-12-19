package com.zst.xposed.halo.floatingwindow;

import com.zst.xposed.halo.floatingwindow.hooks.HaloFloating;
import com.zst.xposed.halo.floatingwindow.hooks.MovableWindow;
import com.zst.xposed.halo.floatingwindow.hooks.NotificationShadeHook;
import com.zst.xposed.halo.floatingwindow.hooks.SystemMods;
import com.zst.xposed.halo.floatingwindow.hooks.SystemUIReceiver;

import android.content.res.XModuleResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainXposed implements IXposedHookLoadPackage, IXposedHookZygoteInit {
	
	public static XModuleResources sModRes;
	static String MODULE_PATH = null;
	static XSharedPreferences mPref;
	static XSharedPreferences mBlacklist;
	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		mPref = new XSharedPreferences(Common.THIS_PACKAGE_NAME, Common.PREFERENCE_MAIN_FILE);
		mBlacklist = new XSharedPreferences(Common.THIS_PACKAGE_NAME, Common.PREFERENCE_BLACKLIST_FILE);
		MODULE_PATH = startupParam.modulePath;
		sModRes = XModuleResources.createInstance(MODULE_PATH, null);
		NotificationShadeHook.zygote(sModRes);
	}
	
	@Override
	public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
		NotificationShadeHook.hook(lpparam, mPref);
		SystemMods.handleLoadPackage(lpparam, mPref);
		SystemUIReceiver.handleLoadPackage(lpparam);
		
		if (isBlacklisted(lpparam.packageName)) return;
		MovableWindow.handleLoadPackage(lpparam, mPref, sModRes);
		HaloFloating.handleLoadPackage(lpparam, mPref);
	}

	private boolean isBlacklisted(String pkg) {
		mBlacklist.reload();
		return mBlacklist.contains(pkg);
	}
}
