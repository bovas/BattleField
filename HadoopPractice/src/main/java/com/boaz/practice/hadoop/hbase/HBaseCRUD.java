package com.boaz.practice.hadoop.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.DependentColumnFilter;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.ValueFilter;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseCRUD {
	
	public static Configuration configuration;  
    static {  
        configuration = HBaseConfiguration.create();  
        configuration.set("hbase.zookeeper.property.clientPort", "2181");  
        configuration.set("hbase.zookeeper.quorum", "localhost");  
    }  

	public static void main(String[] args) throws IOException {

	  createTable("Patient_JAVA");  
      insertData("Patient_JAVA");  
      QueryAll("Patient_JAVA");  
       //QueryByCondition1("Patient_JAVA");  
        // QueryByCondition2("Patient-New");  
        //QueryByCondition3("Patient-New");  
        //deleteRow("Patient-New","abcdef");  
       // deleteByCondition("Patient-New","abcdef"); 
        
      //  RowFilterUsage("Patient-New");
        
        //FamilyFilterUsage("Patient-New");
        
        //QualifierFilterUsage("Patient-New");
        
		//ValueFilterUsage("Patient-New");
	}

	
	public static void createTable(String tableName) {  
        System.out.println("start create table ......");  
        try {  
            HBaseAdmin hBaseAdmin = new HBaseAdmin(configuration);  
            if (hBaseAdmin.tableExists(tableName)) { 
                hBaseAdmin.disableTable(tableName);  
                hBaseAdmin.deleteTable(tableName);  
                System.out.println(tableName + " is exist,detele....");  
            }  
            HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);  
            tableDescriptor.addFamily(new HColumnDescriptor("personal")); 
            tableDescriptor.addFamily(new HColumnDescriptor("medical"));  
            hBaseAdmin.createTable(tableDescriptor);  
        } catch (MasterNotRunningException e) {  
            e.printStackTrace();  
        } catch (ZooKeeperConnectionException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        System.out.println("end create table ......");  
    }  
  
      
    public static void insertData(String tableName) throws IOException {  
        System.out.println("start insert data ......");  
        HTable table = new HTable(configuration, tableName);  
        Put put = new Put("002_Siva".getBytes());
        put.add("personal".getBytes(), "Name".getBytes(), "Siva".getBytes());// ????????  
        put.add("personal".getBytes(), "Age".getBytes(), "29".getBytes());// ????????  
        try {  
            table.put(put);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        System.out.println("end insert data ......");  
    }  
  
      
    public static void dropTable(String tableName) {  
        try {  
            HBaseAdmin admin = new HBaseAdmin(configuration);  
            admin.disableTable(tableName);  
            admin.deleteTable(tableName);  
        } catch (MasterNotRunningException e) {  
            e.printStackTrace();  
        } catch (ZooKeeperConnectionException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
    }  
      
     public static void deleteRow(String tablename, String rowkey)  {  
        try {  
            HTable table = new HTable(configuration, tablename);  
            List list = new ArrayList();  
            Delete d1 = new Delete(rowkey.getBytes());  
            list.add(d1);  
            //table.delete(d1);
            table.delete(list);  
            System.out.println("?????!");  
              
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
          
  
    }  
  
       
     public static void deleteByCondition(String tablename, String rowkey)  {  
            //??????????API???????rowkey?????????????????????API??  
  
    }  
  
  
      
    public static void QueryAll(String tableName) throws IOException {  
        HTable table = new HTable(configuration, tableName);   
        try {  
            ResultScanner rs = table.getScanner(new Scan());  
            for (Result r : rs) {  
                System.out.println("???rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("??" + new String(keyValue.getFamily())  
                            + "====?:" + new String(keyValue.getValue()));  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
      
    public static void QueryByCondition1(String tableName) throws IOException {  
  
        HTable table = new HTable(configuration, tableName);   
        try {  
            Get get = new Get("002_Siva".getBytes());
            get.setMaxVersions(2);
            // ??rowkey??  
            Result r = table.get(get); 
          System.out.println( Bytes.toString( r.getValue("personal".getBytes(), "Name".getBytes())));
           
            System.out.println("???rowkey:" + new String(r.getRow()));  
            for (KeyValue keyValue : r.raw()) {  
                System.out.println("??" + new String(keyValue.getFamily())  
                        + "====?:" + new String(keyValue.getValue()));  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
      
    public static void QueryByCondition2(String tableName) {  
  
        try {  
        	HTable table = new HTable(configuration, tableName);
            Filter filter = new SingleColumnValueFilter(Bytes  
                    .toBytes("personal"), Bytes  
                    .toBytes("Name"), CompareOp.EQUAL, Bytes  
                    .toBytes("Siva")); // ??column1???aaa?????  
            Scan s = new Scan();  
            //s.setFilter(filter);  
            ResultScanner rs = table.getScanner(s);  
            for (Result r : rs) {  
                System.out.println("???rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("??" + new String(keyValue.getFamily())  
                            + "====?:" + new String(keyValue.getValue()));  
                }  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    }  
  
      
    public static void QueryByCondition3(String tableName) {  
  
        try {  
        	HTable table = new HTable(configuration, tableName); 
  
            List<Filter> filters = new ArrayList<Filter>();  
  
            Filter filter1 = new SingleColumnValueFilter(Bytes  
                    .toBytes("personal"), Bytes  
                    .toBytes("Name"), CompareOp.EQUAL, Bytes  
                    .toBytes("Siva"));  
            filters.add(filter1);  
            
            Filter filter2 = new SingleColumnValueFilter(Bytes  
                    .toBytes("medical"), Bytes  
                    .toBytes("DName"), CompareOp.EQUAL, Bytes  
                    .toBytes("Para1"));  
            filters.add(filter2);  
  
            FilterList filterList1 = new FilterList(Operator.MUST_PASS_ALL,filters);
            
           // FilterList filterList1 = new FilterList(Operator.MUST_PASS_ONE,filters);
  
            Scan scan = new Scan();  
            scan.setFilter(filterList1);  
            ResultScanner rs = table.getScanner(scan);  
            for (Result r : rs) {  
                System.out.println("???rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("??" + new String(keyValue.getFamily())  
                            + "====?:" + new String(keyValue.getValue()));  
                }  
            }  
            rs.close();  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    } 
    
    public static void RowFilterUsage(String tableName) {  
    	  
        try {  
        	HTable table = new HTable(configuration, tableName); 
  
            List<Filter> filters = new ArrayList<Filter>();  
  
            Filter filter1 = new RowFilter(CompareOp.EQUAL,new SubstringComparator("001")); 
            filters.add(filter1);
  
            FilterList filterList1 = new FilterList(Operator.MUST_PASS_ALL,filters);
            
           // FilterList filterList1 = new FilterList(Operator.MUST_PASS_ONE,filters);
  
            Scan scan = new Scan();  
            scan.setFilter(filterList1);  
            ResultScanner rs = table.getScanner(scan);  
            for (Result r : rs) {  
                System.out.println("???rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("??" + new String(keyValue.getFamily())  
                            + "====?:" + new String(keyValue.getValue()));  
                }  
            }  
            rs.close();  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    }  
    public static void FamilyFilterUsage(String tableName) {  
  	  
        try {  
        	HTable table = new HTable(configuration, tableName); 
  
            List<Filter> filters = new ArrayList<Filter>();  
  
            Filter filter1 = new FamilyFilter(CompareOp.EQUAL,new SubstringComparator("personal")); 
            filters.add(filter1);
  
            FilterList filterList1 = new FilterList(Operator.MUST_PASS_ALL,filters);
            
           // FilterList filterList1 = new FilterList(Operator.MUST_PASS_ONE,filters);
  
            Scan scan = new Scan();  
            scan.setFilter(filterList1);  
            ResultScanner rs = table.getScanner(scan);  
            for (Result r : rs) {  
                System.out.println("???rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("??" + new String(keyValue.getFamily())  
                            + "====?:" + new String(keyValue.getValue()));  
                }  
            }  
            rs.close();  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    } 
  
    
    public static void QualifierFilterUsage(String tableName) {  
    	  
        try {  
        	HTable table = new HTable(configuration, tableName); 
  
            List<Filter> filters = new ArrayList<Filter>();  
  
            Filter filter1 = new QualifierFilter(CompareOp.EQUAL,new SubstringComparator("DName")); 
            filters.add(filter1);
  
            FilterList filterList1 = new FilterList(Operator.MUST_PASS_ALL,filters);
            
           // FilterList filterList1 = new FilterList(Operator.MUST_PASS_ONE,filters);
  
            Scan scan = new Scan();  
            scan.setFilter(filterList1);  
            ResultScanner rs = table.getScanner(scan);  
            for (Result r : rs) {  
                System.out.println("???rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("??" + new String(keyValue.getFamily())  
                            + "====?:" + new String(keyValue.getValue()));  
                }  
            }  
            rs.close();  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    } 
    
    
    public static void ValueFilterUsage(String tableName) {  
  	  
        try {  
        	HTable table = new HTable(configuration, tableName); 
  
            List<Filter> filters = new ArrayList<Filter>();  
  
            Filter filter1 = new ValueFilter(CompareOp.EQUAL, new SubstringComparator("Siva"));
            filters.add(filter1);
  
            FilterList filterList1 = new FilterList(Operator.MUST_PASS_ALL,filters);
            
           // FilterList filterList1 = new FilterList(Operator.MUST_PASS_ONE,filters);
  
            Scan scan = new Scan();  
            scan.setFilter(filterList1);  
            ResultScanner rs = table.getScanner(scan);  
            for (Result r : rs) {  
                System.out.println("???rowkey:" + new String(r.getRow()));  
                for (KeyValue keyValue : r.raw()) {  
                    System.out.println("??" + new String(keyValue.getFamily())  
                            + "====?:" + new String(keyValue.getValue()));  
                }  
            }  
            rs.close();  
  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    } 
    
} 

