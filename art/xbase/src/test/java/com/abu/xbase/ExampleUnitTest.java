package com.abu.xbase;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;

import com.abu.xbase.util.XUtil;

import org.junit.Test;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    public class DiffCallBack extends DiffUtil.Callback{

        private List<String> mOldDatas, mNewDatas;

        public DiffCallBack(List<String> mOldDatas, List<String> mNewDatas) {
            this.mOldDatas = mOldDatas;
            this.mNewDatas = mNewDatas;
        }

        @Override
        public int getOldListSize() {
            return mOldDatas != null ? mOldDatas.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return mNewDatas != null ? mNewDatas.size() : 0;
        }

        //是不是同一个item 看id
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return XUtil.equals(mOldDatas.get(oldItemPosition), mNewDatas.get(newItemPosition));
        }

        //同一个id的item 是不是内容相同
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return XUtil.equals(mOldDatas.get(oldItemPosition), mNewDatas.get(newItemPosition));
        }

        //该方法在DiffUtil高级用法中用到 ，暂且不提
        @Nullable
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return null;
        }

    }
    public interface BlogService {
        @GET("api/foreign/area/city/list")
        Call<ResponseBody> getList();
    }
    public static class MyBeaan{
        public String v1, v2;

        public String getV1() {
            return v1;
        }

        public void setV1(String v1) {
            this.v1 = v1;
        }

        public String getV2() {
            return v2;
        }

        public void setV2(String v2) {
            this.v2 = v2;
        }
    }
    private static final SimpleDateFormat formatIn =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat formatOut =
            new SimpleDateFormat("MM-dd EEEE", Locale.getDefault());
    private Object getStr(int i){
        if(i==0)
            return "xxxx";
        return null;
    }
    @Test
    public void addition_isCorrect() throws Exception {
        if(true){
            String dataMaxStr = "2018-03-26 12:15:00";
            Date dataMax = formatIn.parse("2018-03-20 21:15:00.0");
            Date dataCurr = new Date(System.currentTimeMillis());
            if(dataCurr.after(dataMax))
                return;


            DecimalFormat myformat = new DecimalFormat();
            myformat.applyPattern("##,###");
            System.out.println(myformat.format(806829198));
            System.out.println(String.format(Locale.getDefault(),
                    "%s开奖走势", null));
            String xx = (String)getStr(1);
            System.out.println((xx == null)+ "--"+"1,".split(",").length);
            String str0 = "%5.2f";
            System.out.println(String.format(Locale.getDefault(), str0, 6.111));
            System.out.println("--"+formatOut.format(formatIn.parse("2018-03-20 21:15:00.0")));
            return;
        }
        if(true){

            System.out.println("1519666100552".length());
            System.out.println((System.currentTimeMillis()+"").length());

            byte b = (byte) 0xa1;
            System.out.println("--"+(b));
            System.out.println("--"+(int)(b));
            System.out.println("--"+(b & 0xff));
            System.out.println("--"+(int)(b & 0xff) );
            System.out.println("83:CB:29:B1:47:9A:62:62:32:16:DD:F2:3B:C6:55:E4"
                    .replaceAll(":", "")
            .toLowerCase());
            System.out.println("98:17:C9:BF:55:7C:AE:79:99:91:6E:E2:77:BC:D6:AE:1E:4E:D6:B6"
                    .replaceAll(":", "")
                    .toLowerCase());

            System.out.println("F5:AC:7E:18:84:D1:5B:A5:88:B2:3D:08:E8:2D:5D:35:12:8C:D7:F3:35:A3:21:D6:10:22:6D:9C:1E:28:CD:76"
                    .replaceAll(":", "")
                    .toLowerCase());
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://111.231.117.129:8080/")
                .build();
        BlogService service = retrofit.create(BlogService.class);
        Call<ResponseBody> call = service.getList();
        // 用法和OkHttp的call如出一辙,
        // 不同的是如果是Android系统回调方法执行在主线程
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    System.out.println("0000000");
                    System.out.println(response.body().string());
                } catch (IOException e) {
                    System.out.println("1111111");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("22222222");
                t.printStackTrace();
            }
        });

        Thread.sleep(10000);
        String str = String.format(Locale.getDefault(), "sss%1$dxxx", 0);
        System.out.println("str::"+str);

        ListUpdateCallback listUpdateCallback = new ListUpdateCallback(){

            @Override
            public void onInserted(int position, int count) {
                System.out.println("onInserted:"+position+"-"+count);
            }

            @Override
            public void onRemoved(int position, int count) {
                System.out.println("onRemoved:"+position+"-"+count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                System.out.println("onMoved:"+fromPosition+"-"+toPosition);
            }

            @Override
            public void onChanged(int position, int count, Object payload) {
                System.out.println("onChanged:"+position+"-"+count+"-"+(payload == null));
                if(payload != null){
                    System.out.println("-"+payload.getClass().getSimpleName()+"-"+payload.toString());
                }
            }
        };
        /**http://blog.csdn.net/zxt0601/article/details/52562770*/
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        list.add("111");
        list.add("222");
        list.add("333");
        list2.add("111");
        list2.add("333");
        list2.add("444");
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallBack(list, list2), true);
        diffResult.dispatchUpdatesTo(listUpdateCallback);

        System.out.println("list::"+list+"\n"+list2);


    }
}