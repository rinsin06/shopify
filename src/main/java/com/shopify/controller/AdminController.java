package com.shopify.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.shopify.Dto.ProductDTO;
import com.shopify.model.Category;
import com.shopify.model.Coupon;
import com.shopify.model.MonthData;
import com.shopify.model.Order;
import com.shopify.model.Product;
import com.shopify.model.ProductImage;
import com.shopify.model.User;
import com.shopify.orderServiceImpl.OrderServiceImpl;
import com.shopify.repository.ProductRepository;
import com.shopify.repository.UserRepository;
import com.shopify.service.CategoryService;
import com.shopify.service.CouponService;
import com.shopify.service.OrderService;
import com.shopify.service.ProductService;
import com.shopify.service.UserService;
import com.shopify.util.CancelledReportGenerator;
import com.shopify.util.OrderStatus;
import com.shopify.util.SalesReportGenerator;
import com.shopify.util.StockReportGenerator;


@Controller
@RequestMapping("/admin")
public class AdminController {
	
	
	 @Autowired
	    CategoryService categoryService;

	    @Autowired
	    ProductService productService;

	
	  public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";

	    @Autowired
	    UserService userService;
	    
	    @Autowired
	    UserRepository repository;
	    
	    @Autowired
	    ProductRepository productRepository;

	    @Autowired
	    private OrderService orderService;
//
	    @Autowired
	    private CouponService couponService;
	    
	    @Autowired
	    OrderServiceImpl orderServiceImpl;

	    @Autowired
	    StockReportGenerator stockReportGenerator;
	    
	    @Autowired
	    CancelledReportGenerator cancelledReportGenerator;
	    
	    @Autowired
	    SalesReportGenerator salesReportGenerator;
	    
	    

	    //    Users Manage Section Starts Here

	    @GetMapping("/users")
	    public String getUsers(Model model){
	    	List<User> users = userService.getAllUser();
	        model.addAttribute("users", users);
	        model.addAttribute("pageTitle", "Users Control | Admin");
	        return findPaginatedUsers(1,model);
	    }
	    
	    @GetMapping("users/page/{pageNo}")
		public String findPaginatedUsers(@PathVariable(value = "pageNo") int pageNo, Model model) {

			int pageSize = 10;
			Page<User> page = userService.findPaginated(pageNo, pageSize);
			List<User> users = page.getContent();

			model.addAttribute("currentPage", pageNo);
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("users", users);
			return "users";
		}
	    
	    @GetMapping("/searchusers")
	    public String searchUsers(Model model,@RequestParam(name="keyword" ,required=false) String keyword){
	    	
	    	List<User> listUsers=  userService.searchUsers(keyword);
	        model.addAttribute("users", listUsers);
	        model.addAttribute("pageTitle", "Products | Admin");
	        model.addAttribute("keyword",keyword);
	        return "users";
	    }
	    
	    @PostMapping("/block/{userId}")
	    public ResponseEntity<String> blockUser(@PathVariable Long userId) {
	        User blockedUser = userService.blockUser(userId);
	        if (blockedUser != null) {
	            return ResponseEntity.ok("User blocked successfully");
	        }
	        return ResponseEntity.notFound().build();
	    }

	    @PostMapping("/unblock/{userId}")
	    public ResponseEntity<String> unblockUser(@PathVariable Long userId) {
	        User unblockedUser = userService.unblockUser(userId);
	        if (unblockedUser != null) {
	            return ResponseEntity.ok("User unblocked successfully");
	        }
	        return ResponseEntity.notFound().build();
	    }


	    @GetMapping
	    public String adminDashboard(Model model){
	        int totalUsers = userService.getTotalUsers();
	        model.addAttribute("totalUsers", totalUsers);
	        int totalProducts = productService.getTotalProducts();
	        model.addAttribute("totalProducts", totalProducts);
	        int totalCategories = categoryService.getTotalCategories();
	        model.addAttribute("totalCategories", totalCategories);
	        int totalOrders = orderService.getTotalOrders();
	        model.addAttribute("totalOrders", totalOrders);
	        List<Product> lowStockProducts = productService.getProductsLowStock();
	        model.addAttribute("lowStockProducts", lowStockProducts);
	        List<Order> recentOrders = orderService.getRecentOrders();
	        OrderStatus cancelledStatus = OrderStatus.CANCELED;
            OrderStatus returnedStatus = OrderStatus.RETURN;
	        List<Order> salesorders = orderServiceImpl.findOrderNotCancelledNotReturnes(cancelledStatus, returnedStatus);
	        int sum =0;
	        for(Order orders:salesorders)
	        {
	        	sum+=orders.getTotalAmount();
	        }
	        model.addAttribute("totalEarnings", sum);
	        model.addAttribute("recentOrders", recentOrders);
	        model.addAttribute("pageTitle", "Admin Dashboard | Admin");
	        return "adminDashboard";
	    }
	    
	    
	    @GetMapping("/salesReport")
	    public void genarateSaleReport(   @RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
	    	    @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,HttpServletResponse response) {
	    	response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=sale_report_.pdf");
            try {
				salesReportGenerator.generateSalesReport( response.getOutputStream(),startDate,endDate);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }

	    @GetMapping("/stockReport")
	    public void genarateStockReport(HttpServletResponse response) {
	    	response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=stock_report_.pdf");
            try {
				stockReportGenerator.generateStockReporte( response.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
	    
	    @GetMapping("/cancelledReport")
	    public void genarateCancelledReport(@RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
	    	    @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate, HttpServletResponse response) {
	    	response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=cancellation_report_.pdf");
            try {
            	cancelledReportGenerator.generateCancelledReport( response.getOutputStream(),startDate,endDate);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }

//	    Category Section Starts Here

	    @GetMapping("/categories")
	    public String getCategories(Model model){
	        model.addAttribute("categories", categoryService.getAllCategory());
	        model.addAttribute("pageTitle", "Categories | Admin");
	        return findPaginatedCategories(1,model);
	    }
	    
	    @GetMapping("categories/page/{pageNo}")
		public String findPaginatedCategories(@PathVariable(value = "pageNo") int pageNo, Model model) {

			int pageSize = 10;
			Page<Category> page = categoryService.findPaginated(pageNo, pageSize);
			List<Category> categories = page.getContent();

			model.addAttribute("currentPage", pageNo);
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("categories", categories);
			return "categories";
		}

	    @GetMapping("/categories/add")
	    public String getCategoriesAdd(Model model){
	        model.addAttribute("category", new Category());
	        model.addAttribute("pageTitle", "Add New Category | Admin");
	        return "categoriesAdd";
	    }

	    @PostMapping("/categories/add")
	    public String postCategoriesAdd(@ModelAttribute("category") Category category, Model model){
	        if(categoryService.isCategoryNameExists(category.getName())){
	            model.addAttribute("error", "Category name already exists");
	            return "categoriesAdd";
	        }
	        categoryService.addCategory(category);

	        return "redirect:/admin/categories";
	    }

	    @PostMapping("/categories/addproducts")
	    public String postCategoriesAddFromProducts( @ModelAttribute("newCategory") Category newCategory, Model model){
	        if(categoryService.isCategoryNameExists(newCategory.getName())){
	            model.addAttribute("error", "Category name already exists");
	            return "categoriesAdd";
	        }
	        categoryService.addCategory(newCategory);

	        if (newCategory.getName() != null && !newCategory.getName().isEmpty()) {
	            categoryService.addCategory(newCategory);
	        }

	        return "redirect:/admin/products/add";
	    }

	    @GetMapping("/categories/delete/{id}")
	    public String deleteCategories(@PathVariable int id){
	        categoryService.removeCategoryById(id);
	        return "redirect:/admin/categories";
	    }

	    @GetMapping("/categories/update/{id}")
	    public String updateCategories(@PathVariable int id, Model model){
	        Optional<Category> category = categoryService.getCategoryById(id);
	        model.addAttribute("pageTitle", "Update Category | Admin");
	        if(category.isPresent()){
	            model.addAttribute("category", category.get());
	            return "categoriesAdd";
	        }
	        else{
	            return "404";
	        }
	    }

	    //    Category Section Ends Here

//	    Product Section Starts Here

	    @GetMapping("/products")
	    public String getProducts(Model model){
	        model.addAttribute("products", productService.getAllProduct());
	        model.addAttribute("pageTitle", "Products | Admin");
	        return findPaginated(1, model);
	    }
	    
	    @GetMapping("/page/{pageNo}")
		public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model) {

			int pageSize = 10;
			Page<Product> page = productService.findPaginated(pageNo, pageSize);
			List<Product> products = page.getContent();

			model.addAttribute("currentPage", pageNo);
			model.addAttribute("totalPages", page.getTotalPages());
			model.addAttribute("totalItems", page.getTotalElements());
			model.addAttribute("products", products);
			return "adminproducts";
		}
	    
	    @GetMapping("/searchproducts")
	    public String searchProducts(Model model,@RequestParam(name="keyword" ,required=false) String keyword){
	    	
	    	List<Product> listProducts=  productService.searchProducts(keyword);
	        model.addAttribute("products", listProducts);
	        model.addAttribute("pageTitle", "Products | Admin");
	        model.addAttribute("keyword",keyword);
	        return "adminproducts";
	    }

	    @GetMapping("/products/details/{productId}")
	    public String getProductDetails(@PathVariable Long productId, Model model) {
	        Optional<Product> product = productService.getProductById(productId);
	        if (product.isPresent()) {
	            model.addAttribute("product", product.get());
	            model.addAttribute("pageTitle", "Product Details | Admin");
	            return "productDetails";
	        } else {
	            return "404";
	        }
	    }


	    @GetMapping("/products/add")
	    public String getProductsAdd(Model model){
	        model.addAttribute("productDTO", new ProductDTO());
	        model.addAttribute("newCategory", new Category());

	        model.addAttribute("categories", categoryService.getAllCategory());
	        model.addAttribute("pageTitle", "Add New Product | Admin");
	        return "productsAdd";
	    }

	    @PostMapping("/products/add")
	    public String postProductsAdd(@ModelAttribute("productDTO") ProductDTO productDTO,
	                                  @RequestParam("productImages") List<MultipartFile> files,
	                                  @RequestParam("imgNames") List<String> imgNames, Model model) throws IOException {

	        Product product = new Product();
	        product.setId(productDTO.getId());
	        product.setName(productDTO.getName());
	        product.setCategory(categoryService.getCategoryById(productDTO.getCategoryId()).get());
	        product.setPrice(productDTO.getPrice());
	        product.setStockQuantity(productDTO.getStockQuantity());
	        product.setWeight(productDTO.getWeight());
	        product.setDescription(productDTO.getDescription());

	        if(productService.isProductNameExists(product.getName())){
	            model.addAttribute("error", "Product name already exists");
	            return "productsAdd";
	        }

	        List<ProductImage> images = new ArrayList<>();

	        for (int i = 0; i < files.size(); i++) {
	            MultipartFile file = files.get(i);
	            String imageUUID;

	            if (!file.isEmpty()) {
	                imageUUID = file.getOriginalFilename();
	                Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
	                Files.write(fileNameAndPath, file.getBytes());
	            } else {
	                if (i < imgNames.size()) {
	                    imageUUID = imgNames.get(i);
	                } else {
	                    imageUUID = "images/no_image_available.png";
	                }
	            }

	            ProductImage image = new ProductImage();
	            image.setImageName(imageUUID);
	            images.add(image);
	        }

	        for (ProductImage image : images) {
	            Long imageId = productService.saveImageAndGetId(image.getImageName());
	        }

	        product.setImages(images);
	        productService.addProduct(product);

	        return "redirect:/admin/products";
	    }
	    @PostMapping("/products/update")
	    public String postProductsUpdate(@ModelAttribute("productDTO") ProductDTO productDTO,
	                                  @RequestParam("productImages") List<MultipartFile> files,
	                                  @RequestParam("imgNames") List<String> imgNames, Model model) throws IOException {

	        Product product = new Product();
	        product.setId(productDTO.getId());
	        product.setName(productDTO.getName());
	        product.setCategory(categoryService.getCategoryById(productDTO.getCategoryId()).get());
	        product.setPrice(productDTO.getPrice());
	        product.setStockQuantity(productDTO.getStockQuantity());
	        product.setWeight(productDTO.getWeight());
	        product.setDescription(productDTO.getDescription());

//	        if(productService.isProductNameExists(product.getName())){
//	            model.addAttribute("error", "Product name already exists");
//	            return "productsAdd";
//	        }

	        List<ProductImage> images = new ArrayList<>();

	        for (int i = 0; i < files.size(); i++) {
	            MultipartFile file = files.get(i);
	            String imageUUID;

	            if (!file.isEmpty()) {
	                imageUUID = file.getOriginalFilename();
	                Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
	                Files.write(fileNameAndPath, file.getBytes());
	            } else {
	                if (i < imgNames.size()) {
	                    imageUUID = imgNames.get(i);
	                } else {
	                    imageUUID = "images/no_image_available.png";
	                }
	            }

	            ProductImage image = new ProductImage();
	            image.setImageName(imageUUID);
	            images.add(image);
	        }

	        for (ProductImage image : images) {
	            Long imageId = productService.saveImageAndGetId(image.getImageName());
	        }

	        product.setImages(images);
	        productService.addProduct(product);

	        return "redirect:/admin/products";
	    }

	    @GetMapping("/product/delete/{id}")
	    public String deleteProduct(@PathVariable long id){
	        productService.removeProductById(id);
	        return "redirect:/admin/products";
	    }

	    @GetMapping("/product/update/{id}")
	    public String updateProduct(@PathVariable long id, Model model){
	        Product product = productService.getProductById(id).orElseThrow(() -> new RuntimeException("Product not found"));
	             ProductDTO productDTO = new ProductDTO();
	        productDTO.setId(product.getId());
	        productDTO.setName(product.getName());
	        productDTO.setCategoryId(product.getCategory().getId());
	        Category category = product.getCategory();
	        if (category != null) {
	            productDTO.setCategoryId(category.getId());
	        }
	        productDTO.setPrice(product.getPrice());
	        productDTO.setStockQuantity(product.getStockQuantity());
	        productDTO.setWeight(product.getWeight());
	        productDTO.setDescription(product.getDescription());
	        productDTO.setImageNames(product.getImages().stream().map(ProductImage::getImageName).collect(Collectors.toList()));

	        model.addAttribute("categories", categoryService.getAllCategory());
	        model.addAttribute("productDTO", productDTO);
	        model.addAttribute("pageTitle", "Update Product | Admin");

	        return "updateproduct";
	    }
	    
//      Orders Management

  @GetMapping("/orders")
  public String getOrders(Model model) {
      List<Order> orders = orderService.getAllOrders();
      List<Order> reversedOrders = new ArrayList<>();

	     // Iterate through the original list in reverse order
	     for (int i = orders.size() - 1; i >= 0; i--) {
	         Order order = orders.get(i);
	         // Add the order to the new list
	         reversedOrders.add(order);
	     }
      model.addAttribute("orders", reversedOrders);
      model.addAttribute("pageTitle", "Orders | Admin");
      return "orders";
  }
   

  @GetMapping("/orders/details/{orderId}")
  public String getOrderDetails(@PathVariable Long orderId, Model model) {
      Optional<Order> order = orderService.getOrderById(orderId);
      if (order.isPresent()) {
          model.addAttribute("order", order.get());
          model.addAttribute("pageTitle", "Order Details | Admin");
          return "orderDetails";
      } else {
          return "404";
      }
  }

  @PostMapping("/orders/cancel/{orderId}")
  public String cancelOrder(@PathVariable Long orderId) {
      Optional<Order> order = orderService.getOrderById(orderId);
      if (order.isPresent()) {
          Order cancelOrder = order.get();
          cancelOrder.setStatus(OrderStatus.CANCELED);
          orderService.saveOrder(cancelOrder);
      }
      return "redirect:/admin/orders";
  }

//  @PostMapping("/orders/update-status")
//  public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
//      Optional<Order> optionalOrder = orderService.getOrderById(orderId);
//      if (optionalOrder.isPresent()) {
//          Order order = optionalOrder.get();
//
//          try {
//              OrderStatus newStatus = OrderStatus.valueOf(status);
//              order.setStatus(newStatus);
//              orderService.saveOrder(order);
//          } catch (IllegalArgumentException e) {
//              // Handle invalid status value
//          }
//      }
//      return "redirect:/admin/orders";
//  }

  @PostMapping("/orders/update-status")
  public String updateOrderStatus(@RequestParam Long orderId, @RequestParam OrderStatus newStatus, @RequestParam String placeReached) {
      Optional<Order> optionalOrder = orderService.getOrderById(orderId);
      if (optionalOrder.isPresent()) {
          Order order = optionalOrder.get();
          order.updateOrderStatusAndPlace(newStatus, placeReached);
          orderService.saveOrder(order);
      }
      return "redirect:/admin/orders";
  }
  
  // Coupon or discount management

  @GetMapping("/coupons")
  public String listCoupons(Model model) {
      List<Coupon> coupons = couponService.getAllCoupons();
      model.addAttribute("coupons", coupons);
      return "couponsList";
  }

  @GetMapping("/coupons/add")
  public String showCouponForm(Model model) {
      model.addAttribute("coupon", new Coupon());
      return "couponAdd";
  }

  @PostMapping("/coupons/add")
  public String saveCoupon(@ModelAttribute Coupon coupon) {
      couponService.saveCoupon(coupon);
      return "redirect:/admin/coupons";
  }

  @GetMapping("/coupons/edit/{id}")
  public String editCouponForm(@PathVariable Long id, Model model) {
      Optional<Coupon> coupon = couponService.getCouponById(id);
      if (coupon.isPresent()) {
          model.addAttribute("coupon", coupon.get());
          return "couponEdit";
      } else {
          return "404";
      }
      
      
  }

  @PostMapping("/coupons/edit/{id}")
  public String editCoupon(@PathVariable Long id, @ModelAttribute Coupon editedCoupon) {
      Optional<Coupon> coupon = couponService.getCouponById(id);
      if (coupon.isPresent()) {
          Coupon existingCoupon = coupon.get();
          existingCoupon.setCode(editedCoupon.getCode());
          existingCoupon.setDiscountPercentage(editedCoupon.getDiscountPercentage());
          existingCoupon.setStartDate(editedCoupon.getStartDate());
          existingCoupon.setEndDate(editedCoupon.getEndDate());

          couponService.saveCoupon(existingCoupon);
          return "redirect:/admin/coupons";
      } else {
          return "404";
      }
  }


  @GetMapping("/coupons/delete/{id}")
  public String deleteCoupon(@PathVariable Long id) {
      couponService.deleteCoupon(id);
      return "redirect:/admin/coupons";
  }
  
  @GetMapping("/chart")
  public String chart(@RequestParam(name="selectedYear",required = false) Integer selectedYear,Model model) {
	  if(selectedYear == null)
	  {
		  selectedYear = 2023;
	  }
      List<Double> chartData = orderServiceImpl.findMonthlyEarnings(selectedYear);
      
      List<Object[]> rawResults = orderServiceImpl.listOfMonthsWithOrders(selectedYear);
      List<MonthData> monthDataList = new ArrayList<>();

      for (Object[] result : rawResults) {
          String monthName = (String) result[0];
          int monthNumber = (int) result[1];
          monthDataList.add(new MonthData(monthName, monthNumber));
      }// Your chart data
     
      List<String> chartLabels = monthDataList.stream()
    		  .map(monthData -> "\"" + monthData.getMonthName() + "\"")
    	        .collect(Collectors.toList());
      List<Integer> years = orderServiceImpl.listOfYears();
      model.addAttribute("chartData", chartData);
      model.addAttribute("chartLabels", chartLabels);
      model.addAttribute("years", years);
      model.addAttribute("selectedYear", selectedYear);

      return "chart"; // Thymeleaf template name
  }

  @GetMapping("/chart2")
  public String chart2(Model model) {
      List<Double> chartData = orderServiceImpl.earningsPerYear();
      
      List<Object[]> rawResults = orderServiceImpl.listOfMonthsWithOrders(2023);
      List<MonthData> monthDataList = new ArrayList<>();

      for (Object[] result : rawResults) {
          String monthName = (String) result[0];
          int monthNumber = (int) result[1];
          monthDataList.add(new MonthData(monthName, monthNumber));
      }// Your chart data
     
//      List<String> chartLabels = monthDataList.stream()
//    		  .map(monthData -> "\"" + monthData.getMonthName() + "\"")
//    	        .collect(Collectors.toList());
      
      List<Integer> chartLabels = orderServiceImpl.listOfYears();

      model.addAttribute("chartData", chartData);
      model.addAttribute("chartLabels", chartLabels);

      return "chart2"; // Thymeleaf template name
  }


}
