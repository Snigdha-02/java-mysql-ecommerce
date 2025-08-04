//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.sql.*;
import java.util.*;

class DBConnection{

    public static Connection getConnection() throws Exception{
        String url = "jdbc:mysql://localhost:3306/ecommerce";
        String name = "root";
        String password = "Snigdha@2004";

        return DriverManager.getConnection(url,name,password);
    }
}
class Product{
    int id;
    String name;
    double cost;
    int stock;
    public Product(int id,String name,double cost,int stock){
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public String toString() {
        return id + "." + name + " - " + cost + "(" + stock + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Double.compare(cost, product.cost) == 0 && stock == product.stock && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, cost, stock);
    }
}

class productDAO{
    List<Product> getallproducts() throws Exception {
        List<Product> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM product");
        while(rs.next()){
            Product p = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("cost"),
                    rs.getInt("stock")
            );
            list.add(p);
        }
        rs.close();
        st.close();
        con.close();
        return list;
    }
    public Product getproductbyId(int id) throws Exception {
        Connection con = DBConnection.getConnection();
        PreparedStatement st = con.prepareStatement("SELECT * FROM product WHERE id = ?");
        st.setInt(1,id);
        ResultSet rs = st.executeQuery();
        if(rs.next()){
           return new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("cost"),
                    rs.getInt("stock")
            );

        }
        rs.close();
        st.close();
        con.close();
        return null;
    }
}

class Cart{
    Map<Product , Integer> cart = new HashMap<>();
    public void addproduct(Product p , int qty){
        cart.put(p , cart.getOrDefault(p , 0) + qty);
    }
    public void removeproduct(Product p , int qty){
        if(cart.containsKey(p)){
            int currqty = cart.get(p);
            if(qty >= currqty){
                cart.remove(p);
            }else{
                cart.put(p,currqty-qty);
            }
        }
    }
    public void viewcart(){
        int total = 0;
        for(Map.Entry<Product,Integer> entry : cart.entrySet()){
            Product p = entry.getKey();
            int qty = entry.getValue();
            double subtotal = qty * p.getCost();
            total += subtotal;
            System.out.println(p + " Quantity: " + qty + " Subtotal: " + subtotal);
        }
        System.out.println("Total: " + total);
    }
}
public class Main {
    public static void main(String[] args) throws Exception{
        productDAO dao = new productDAO();
        Cart cart = new Cart();
        while(true) {
            System.out.println("\n1.View Products.\n2.Add to Cart.\n3.Remove from Cart.\n4.View Cart.\n5.Close.");
            Scanner sc = new Scanner(System.in);
            System.out.println("Choose an option:");
            int choice = sc.nextInt();
            switch(choice) {
                case 1:
                    List<Product> products = dao.getallproducts();
                    for (Product p : products) {
                        System.out.println(p);
                    } break;
                case 2:
                    System.out.println("Enter Product ID.:");
                    int id = sc.nextInt();
                    System.out.println("Enter Product Quantity:");
                    int qty = sc.nextInt();
                    Product addprod = dao.getproductbyId(id);
                    if(addprod != null) {
                        cart.addproduct(addprod, qty);
                        System.out.println("Added to Cart.");
                    }else{
                        System.out.println("Product not found");
                    }
                    break;
                case 3:
                    System.out.println("Enter Product ID.:");
                    int id1 = sc.nextInt();
                    System.out.println("Enter Product Quantity:");
                    int qty1 = sc.nextInt();
                    Product remprod = dao.getproductbyId(id1);
                    if(remprod != null) {
                        cart.removeproduct(remprod,qty1);
                        System.out.println("Removed from Cart.");
                    }else{
                        System.out.println("Product not found");
                    }
                    break;
                case 4:
                    cart.viewcart();
                    break;
                case 5:
                    System.out.println("Good Bye...");
                    System.exit(0);
            }
        }

    }
}