# SwipeItemRecycler

##  关于

* 这个项目主要是实现的Recyclerview中item的滑动菜单。不是类似ios中的那个菜单。而是比较符合material design中的列表滑开菜单。这种控件在support包中并没有，网上的实现也大多是要重写RecyclerView。然而我认为这样并不好，所以就写了这个不需要引入新的RecyclerView的控件。

## 使用

java代码只要给RecyclerView附上这个辅助类就，实现这个辅助类的一个接口。

```java
   public class MyAdapter extends RecyclerView.Adapter implements ItemHelpter.Callback{
    private Context context;
    private LayoutInflater mInflater;
    private RecyclerView mRecycler;

    public MyAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public SwipeLayout getSwipLayout(float x, float y) {
        return (SwipeLayout)mRecycler.findChildViewUnder(x,y);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecycler = recyclerView;
        recyclerView.addOnItemTouchListener(new ItemHelpter(context,this));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_layout,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder holder1 = (MyViewHolder)holder;

        holder1.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "测试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        SwipeLayout root;
        ImageView imageView;
        RelativeLayout mLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            root = (SwipeLayout)itemView;
            imageView = (ImageView)itemView.findViewById(R.id.image_test);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.layout_item);
        }
    }

}
```
在ViewHolder的布局要以这个样子写
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.me.swipeitemrecycler.SwipeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="wrap_content"
    android:layout_height="wrap_content">
    <ImageView
        android:src = "@mipmap/ic_launcher"
        android:layout_width="60dp"
        android:id = "@+id/image_test"
        android:layout_height="60dp" />
    <RelativeLayout
        android:id = "@+id/layout_item"
        android:layout_width="wrap_content"
        android:layout_height="60dp">
        <ImageView
            android:background="#ffffff"
            android:layout_width="match_parent"
            android:layout_height="60dp"
            android:id = "@+id/button"
            android:src = "@mipmap/ic_launcher"/>
    </RelativeLayout>
</com.me.swipeitemrecycler.SwipeLayout>
```

<<<<<<< HEAD
## ???

[![GIFEncoder3.gif](http://imgchr.com/images/GIFEncoder3.gif)](http://imgchr.com/image/7xd)
=======
## 效果图
>>>>>>> 3ccc615eca1e16b084dd9af91ff10fe21ed1ea57

[![GIFEncoder3.gif](http://imgchr.com/images/GIFEncoder3.gif)](http://imgchr.com/image/7xd)


