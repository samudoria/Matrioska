package mirror.android.app;

import android.content.Intent;

import mirror.RefClass;
import mirror.RefObject;

public class ResultInfo {
  public static Class<?> TYPE = RefClass.load(ResultInfo.class, "android.app.ResultInfo");
  public static RefObject<String> mResultWho;
  public static RefObject<Integer> mRequestCode;
  public static RefObject<Integer> mResultCode;
  public static RefObject<Intent> mData;
}
