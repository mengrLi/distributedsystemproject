package GUI.panels.adminpanels;

import lombok.Getter;

import javax.swing.table.DefaultTableModel;
import java.util.LinkedList;
import java.util.List;


public class AdminTableModel extends DefaultTableModel{
    @Getter
    private List<List<Object>> table;
    private String[] header;

    private int selectedRow = 0;

    AdminTableModel(){
        table = new LinkedList<>();
        header = new String[3];
        header[0] = "Index";
        header[1] = "Start";
        header[2] = "End";
    }

    public AdminTableModel(List<List<Object>> table){
        this();
        this.table = table;
    }
    public void addElement(List<Object> innerList){
        table.add(innerList);
    }

    @Override
    public int getRowCount(){
        return table.size();
    }

    @Override
    public int getColumnCount(){
        return header.length;
    }

    @Override
    public String getColumnName(int columnIndex){
        return header[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex){
        return table.get(0).get(columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex){
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex){
        return table.get(rowIndex).get(columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
        if(rowIndex<table.size() && columnIndex<header.length) table.get(rowIndex).set(columnIndex, aValue);
    }

}