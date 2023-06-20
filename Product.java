import java.io.IOException;
import java.io.RandomAccessFile;

public class Product {
    public static final int SIZE = 42;

    private int productId;
    private String productName;
    private int quantity;
    private double unitPrice;

    public Product() {
    }

    public Product(int productId, String productName, int quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void writeToFile(RandomAccessFile file) throws IOException {
        file.writeInt(productId);
        writeString(file, productName, 30);
        file.writeInt(quantity);
        file.writeDouble(unitPrice);
    }

    public void readFromFile(RandomAccessFile file) throws IOException {
        productId = file.readInt();
        productName = readString(file, 30);
        quantity = file.readInt();
        unitPrice = file.readDouble();
    }

    private void writeString(RandomAccessFile file, String value, int length) throws IOException {
        StringBuilder sb = new StringBuilder(length);
        sb.append(value);
        sb.setLength(length);
        file.write(sb.toString().getBytes());
    }

    private String readString(RandomAccessFile file, int length) throws IOException {
        byte[] buffer = new byte[length];
        file.readFully(buffer);
        return new String(buffer).trim();
    }
}
