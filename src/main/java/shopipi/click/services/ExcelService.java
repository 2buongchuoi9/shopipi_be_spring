// package shopipi.click.services;

// import java.io.ByteArrayInputStream;
// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.util.List;

// import org.apache.poi.ss.usermodel.Cell;
// import org.apache.poi.ss.usermodel.CellStyle;
// import org.apache.poi.ss.usermodel.Font;
// import org.apache.poi.ss.usermodel.IndexedColors;
// import org.apache.poi.ss.usermodel.Row;
// import org.apache.poi.ss.usermodel.Sheet;
// import org.apache.poi.ss.usermodel.Workbook;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// import org.springframework.stereotype.Service;

// import shopipi.click.entity.Order;
// import shopipi.click.models.ShopOrderItemsModel;
// import shopipi.click.models.ShopOrderItemsModel.ProductItemsModel;

// @Service
// public class ExcelService {
// public ByteArrayInputStream exportOrdersToExcel(List<Order> orders) {
// String[] columns = { "Mã hóa đơn", "Ngày đặt", "Tình trạng đơn hàng", "Đơn vị
// vận chuyển",
// "Phương thức thanh toán/Trạng thái thanh toán", "Tên sản phẩm", "Tên biến thể
// của sản phẩm",
// "Số lượng mua của sản phẩm", "Giá gốc của biến thể", "Số tiền giảm giá",
// "Số tiền áp dụng voucher", "Tổng giá trị đơn hàng", "Tên người nhận", "Số
// điện thoại",
// "Địa chỉ", "Email người nhận" };

// try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new
// ByteArrayOutputStream()) {
// Sheet sheet = workbook.createSheet("Orders");

// // Create a Font for styling header cells
// Font headerFont = workbook.createFont();
// headerFont.setBold(true);
// headerFont.setColor(IndexedColors.BLACK.getIndex());

// // Create a CellStyle with the font
// CellStyle headerCellStyle = workbook.createCellStyle();
// headerCellStyle.setFont(headerFont);

// // Create a Row
// Row headerRow = sheet.createRow(0);

// // Create cells
// for (int i = 0; i < columns.length; i++) {
// Cell cell = headerRow.createCell(i);
// cell.setCellValue(columns[i]);
// cell.setCellStyle(headerCellStyle);
// }

// // Create Other rows and cells with order data
// int rowNum = 1;

// for (Order order : orders) {
// for (ShopOrderItemsModel item : order.getItems()) {
// for (ProductItemsModel detail : item.getItems()) {
// Row row = sheet.createRow(rowNum++);

// row.createCell(0).setCellValue(order.getId());
// row.createCell(1).setCellValue(order.getCreatedAt().toString());
// row.createCell(2).setCellValue(order.getState());
// row.createCell(3).setCellValue(order.getShippingType());
// row.createCell(4).setCellValue(order.getPayment());
// row.createCell(5).setCellValue(detail.getProduct().getName());
// row.createCell(6).setCellValue(detail.getVariant().getValueVariant().toString());
// row.createCell(7).setCellValue(detail.getQuantity());
// row.createCell(8).setCellValue(detail.getVariant().getPrice());
// row.createCell(9).setCellValue(order.getTotalDiscount());
// row.createCell(10).setCellValue(order.getTotalDiscount()); // Assuming
// voucher discount is the same as total
// // discount
// row.createCell(11).setCellValue(order.getTotalOrder());
// row.createCell(12).setCellValue(order.getUser().getAddress().get(0).getName());
// row.createCell(13).setCellValue(order.getUser().getAddress().get(0).getPhone());
// row.createCell(14).setCellValue(order.getUser().getAddress().get(0).getAddress());
// row.createCell(15).setCellValue(order.getUser().getEmail());
// }
// }
// }

// workbook.write(out);
// return new ByteArrayInputStream(out.toByteArray());
// } catch (IOException e) {
// throw new RuntimeException("Failed to export data to Excel file", e);
// }
// }
// }
