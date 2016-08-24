package com.showcast.hvscroll.draw;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.showcast.hvscroll.entity.TableEntity;
import com.showcast.hvscroll.params.CellParams;
import com.showcast.hvscroll.params.GlobalParams;
import com.showcast.hvscroll.params.MenuParams;

/**
 * Created by taro on 16/8/24.
 */
public interface IHVScrollTable {

    /**
     * 设置表格对象
     *
     * @param table
     */
    public void setTable(TableEntity table);

    /**
     * 设置所有的参数对象.
     *
     * @param global 全局参数对象
     * @param menu   菜单参数对象
     * @param cell   单元格参数对象
     */
    public void setParams(@NonNull GlobalParams global, MenuParams menu, CellParams cell);

    /**
     * 设置全局参数对象,全局参数必须存在.任何时间重新设置都需要一个非null对象.
     *
     * @param params
     */
    public void setGlobalParams(@NonNull GlobalParams params);

    /**
     * 设置菜单对象
     *
     * @param menu
     */
    public void setMenuParams(MenuParams menu);

    /**
     * 设置单元格对象
     *
     * @param cell
     */
    public void setCellParams(CellParams cell);

    /**
     * 获取表格
     *
     * @return
     */
    @Nullable
    public TableEntity getTable();

    /**
     * 获取全局参数对象
     *
     * @return
     */
    @NonNull
    public GlobalParams getGlobalParams();

    /**
     * 获取菜单参数对象
     *
     * @return
     */
    @Nullable
    public MenuParams getMenuParams();

    /**
     * 设置单元格参数对象
     *
     * @return
     */
    @Nullable
    public CellParams getCellParams();
}
