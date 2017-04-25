import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Administrator{
	
	String branchName, 	branchID, 		branchAddr;
	String bookID, 	   	bookName;
	String authorID,    authorName;
	String publisherID, publisherName, 	publisherAddr;
	String borrowerID, 	borrowerName, 	borrowerAddress, borrowerPhone;
	String dateOut, 	dateDue, 		dateIn;
	
}

class AdminProcedure {
	
	/**********************************************/
	String 
	       SQLShowAllBks    = "SELECT * FROM tbl_book b",
	 	   SQLShowAllAus    = "SELECT * FROM tbl_author a",
	 	   SQLShowAllPbs    = "SELECT * FROM tbl_publisher p",
	 	   SQLShowAllBch    = "SELECT * FROM tbl_library_branch lb",
	 	   SQLShowAllBrw	= "SELECT * FROM tbl_borrower br",
	 	   SQLShowAllLoan   = "SELECT * FROM tbl_book_loans bl, tbl_book b, tbl_borrower br, tbl_library_branch lb WHERE "
	 	   		+ "b.bookId=bl.bookId AND br.cardNo=bl.cardNo AND lb.branchId=bl.branchId",
	       SQLUpdateBkName  = "UPDATE tbl_book b SET b.title=? WHERE b.bookId=?",
	       SQLUpdateBkName2 = "UPDATE tbl_book b SET b.title=?, b.pubId=? WHERE b.bookId=?",
	       SQLInsertBkAu    = "INSERT INTO tbl_book_authors VALUE (?,?)",
	       SQLUpdateBkAu    = "UPDATE tbl_book_authors ba SET ba.authorId=? WHERE ba.bookId=?",
	       SQLUpdateAuName  = "UPDATE tbl_author a SET a.authorName=? WHERE a.authorId=?",
	       SQLInsertBkName  = "INSERT INTO tbl_book(title,pubId) VALUE(?,?)",
	       SQLInsertAuName  = "INSERT INTO tbl_author(authorName) VALUE(?)",
		   SQLDeleteBkName  = "DELETE FROM tbl_book WHERE bookId=?",
		   SQLDeleteAuName  = "DELETE FROM tbl_author WHERE authorId=?",
		   SQLInsertPbName  = "INSERT INTO tbl_publisher(publisherName, publisherAddress, publisherPhone) VALUE(?,?,?)",
		   SQLUpdatePbName	= "UPDATE tbl_publisher p SET p.publisherName=?, p.publisherAddress=?, p.publisherPhone=? WHERE p.publisherId=?",
		   SQLDeletePbName  = "DELETE FROM tbl_publisher WHERE publisherId=?",
		   SQLInsertBchName = "INSERT INTO tbl_library_branch(branchName, branchAddress) VALUE(?,?)",
		   SQLUpdateBchName = "UPDATE tbl_library_branch lb SET lb.branchName=?, lb.branchAddress=? WHERE lb.branchId=?",
		   SQLDeleteBchName = "DELETE FROM tbl_library_branch WHERE branchId=?",
		   SQLInsertBrwName = "INSERT INTO tbl_borrower(name, address, phone) VALUE(?,?,?)",
		   SQLUpdateBrwName = "UPDATE tbl_borrower br SET br.name=?, br.address=?, br.phone=? WHERE br.cardNo=?",
		   SQLDeleteBrwName = "DELETE FROM tbl_borrower WHERE cardNo=?",
		   SQLUpdateDueDate = "UPDATE tbl_book_loans bl SET bl.dueDate=? WHERE bl.bookId=? AND bl.branchId=? AND bl.cardNo=?",
		   SQLCheckBkName	= "SELECT * FROM tbl_book b WHERE b.title=?",
		   SQLCheckAuName   = "SELECT * FROM tbl_author a WHERE a.authorName=?",
		   SQLCheckPbName   = "SELECT * FROM tbl_publisher p WHERE p.publisherName=? AND p.publisherAddress=?",
		   SQLCheckBchName  = "SELECT * FROM tbl_library_branch lb WHERE lb.branchName=? AND lb.branchAddress=?";
	
	/**********************************************/	
	
	Scanner in = new Scanner(System.in);
	LibSystem libSys    = new LibSystem();
	String custTab      = "    ";
	Administrator admin = new Administrator();
	
	boolean execute(){
		int selection = 5;
		while (true) {
			System.out.println("Please select operation:");
			System.out.println(custTab + "1) Add");
			System.out.println(custTab + "2) Update");
			System.out.println(custTab + "3) Delete");
			System.out.println(custTab + "4) Over-ride due date for a book loan");
			System.out.println(custTab + "5) Quit to previous");
			selection = in.nextInt();
			if (selection == 5) {
				return false;
			}
			else if (selection == 4) {
				changeDueDate();
				continue;			
			}
			else {
				selectItems(selection);
				continue;
			}
		}
		
	}
	
	boolean selectItems(int operation){
		
		System.out.println("Please select: ");
		System.out.println(custTab + "1) Book");
		System.out.println(custTab + "2) Author");
		System.out.println(custTab + "3) Publishers");
		System.out.println(custTab + "4) Library branches");
		System.out.println(custTab + "5) Borrowers");
		System.out.println(custTab + "6) Quit to previous");
		
		int selection = in.nextInt();
		
		switch (selection) {
			// book operation
			case 1 : {
				ResultSet rs = null;
				List<String> bookNames = new ArrayList<>(), 
						     bookIDs   = new ArrayList<>();
				int displayIndex = 1;
				try {
					rs = libSys.executeSQL(SQLShowAllBks);
					libSys.unholdExecuteSQL();
					while (rs.next()) {
						String bookID = rs.getString("bookId");
						bookIDs.add(bookID);
						String bookName = rs.getString("title");
						bookNames.add(bookName);
						System.out.println(custTab + displayIndex++ + ") " + bookName);
					}
					if (operation != 1) {
						System.out.println(custTab + displayIndex++ + ") Quit to previous");
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
				
				
				if (operation != 1) {
					selection = in.nextInt();
					if (selection == bookIDs.size() + 1) {
						return false;
					} else if (selection < 1 || selection > bookIDs.size() + 1) {
						return false;
					}
					admin.bookID = bookIDs.get(selection - 1);
					admin.bookName = bookNames.get(selection - 1);
				}
				switch(operation){
				//add book name
					case 1: {
						changeItemDetail(1);
						break;
					}
				//update book name
					case 2: {
						changeItemDetail(2);
						break;
					}
				//delete book name
					case 3: {
						changeItemDetail(3);
						break;
					}
				}
				break;
			}
		
			// author operation
			case 2 : {
				int authorOperation = 3;
				ResultSet rs = null;
				List<String> authorNames = new ArrayList<>(), 
						     authorIDs   = new ArrayList<>();
				int displayIndex = 1;
				try {
					rs = libSys.executeSQL(SQLShowAllAus);
					libSys.unholdExecuteSQL();
					while (rs.next()) {
						String authorID = rs.getString("authorId");
						authorIDs.add(authorID);
						String authorName = rs.getString("authorName");
						authorNames.add(authorName);
						System.out.println(custTab + displayIndex++ + ") " + authorName);
					}
					if (operation != 1) {
						System.out.println(custTab + displayIndex++ + ") Quit to previous");
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
				
				if (operation != 1) {
					selection = in.nextInt();
					if (selection == authorIDs.size() + 1) {
						return false;
					} else if (selection < 1 || selection > authorIDs.size() + 1) {
						return false;
					}
					admin.authorID = authorIDs.get(selection - 1);
					admin.authorName = authorNames.get(selection - 1);
				}
				switch(operation){
				//add author name
					case 1: {
						changeItemDetail(authorOperation+operation);
						break;
					}
				//update author name
					case 2: {
						changeItemDetail(authorOperation+operation);
						break;
					}
				//delete author name
					case 3: {
						changeItemDetail(authorOperation+operation);
						break;
					}
				}
				break;
			}
			
			//publisher operation
			case 3: {
				int publisherOperation = 6;
				ResultSet rs = null;
				List<String> publisherNames = new ArrayList<>(), 
						     publisherIDs   = new ArrayList<>(),
							 publisherAddrs = new ArrayList<>();
				int displayIndex = 1;
				try {
					rs = libSys.executeSQL(SQLShowAllPbs);
					libSys.unholdExecuteSQL();
					while (rs.next()) {
						String publisherID = rs.getString("publisherId");
						publisherIDs.add(publisherID);
						String publisherName = rs.getString("publisherName");
						publisherNames.add(publisherName);
						String publisherAddr = rs.getString("publisherAddress");
						publisherAddrs.add(publisherAddr);
						System.out.println(custTab + displayIndex++ + ") " + publisherName + " | " + publisherAddr);
					}
					if (operation != 1) {
						System.out.println(custTab + displayIndex++ + ") Quit to previous");
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
				
				if (operation != 1) {
					selection = in.nextInt();
					if (selection == publisherIDs.size() + 1) {
						return false;
					} else if (selection < 1 || selection > publisherIDs.size() + 1) {
						return false;
					}
					admin.publisherID = publisherIDs.get(selection - 1);
					admin.publisherName = publisherNames.get(selection - 1);
					admin.publisherAddr = publisherAddrs.get(selection - 1);
				}
				switch(operation){
				//add publisher name
					case 1: {
						changeItemDetail(publisherOperation + operation);
						break;
					}
				//update publisher name
					case 2: {
						changeItemDetail(publisherOperation + operation);
						break;
					}
				//delete publisher name
					case 3: {
						changeItemDetail(publisherOperation + operation);
						break;
					}
				}
				break;
			}
			//branch operation
			case 4: {
				int branchOperation = 9;
				ResultSet rs = null;
				List<String> branchNames = new ArrayList<>(), 
							 branchIDs   = new ArrayList<>(),
							 branchAddrs = new ArrayList<>();
				int displayIndex = 1;
				try {
					rs = libSys.executeSQL(SQLShowAllBch);//!
					libSys.unholdExecuteSQL();
					while (rs.next()) {
						String branchID = rs.getString("branchId");
						branchIDs.add(branchID);
						String branchName = rs.getString("branchName");
						branchNames.add(branchName);
						String branchAddr = rs.getString("branchAddress");
						branchAddrs.add(branchAddr);
						System.out.println(custTab + displayIndex++ + ") " + branchName + " | " + branchAddr);
					}
					if (operation != 1) {
						System.out.println(custTab + displayIndex++ + ") Quit to previous");
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
				
				if (operation != 1) {
					selection = in.nextInt();
					if (selection == branchIDs.size() + 1) {
						return false;
					} else if (selection < 1 || selection > branchIDs.size() + 1) {
						return false;
					}
					admin.branchID = branchIDs.get(selection - 1);
					admin.branchName = branchNames.get(selection - 1);
					admin.branchAddr = branchAddrs.get(selection - 1);
				}
				switch(operation){
				//add branch name
					case 1: {
						changeItemDetail(branchOperation + operation);
						break;
					}
				//update branch name
					case 2: {
						changeItemDetail(branchOperation + operation);
						break;
					}
				//delete branch name
					case 3: {
						changeItemDetail(branchOperation + operation);
						break;
					}
				}
				break;
			}
			//borrower operation
			case 5: {
				int borrowerOperation = 12;
				ResultSet rs = null;
				List<String> borrowerNames  = new ArrayList<>(), 
							 borrowerIDs    = new ArrayList<>(),
							 borrowerAddrs  = new ArrayList<>(),
							 borrowerPhones = new ArrayList<>();
				int displayIndex = 1;
				try {
					rs = libSys.executeSQL(SQLShowAllBrw);
					libSys.unholdExecuteSQL();
					while (rs.next()) {
						String borrowerID = rs.getString("cardNo");
						borrowerIDs.add(borrowerID);
						String borrowerName = rs.getString("name");
						borrowerNames.add(borrowerName);
						String borrowerAddr = rs.getString("address");
						borrowerAddrs.add(borrowerAddr);
						String borrowerPhone = rs.getString("phone");
						borrowerPhones.add(borrowerPhone);
						System.out.println(custTab + displayIndex++ + ") " 
								+ borrowerName + " | " + borrowerAddr + " | " + borrowerPhone);
					}
					if (operation != 1) {
						System.out.println(custTab + displayIndex++ + ") Quit to previous");
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
				
				if (operation != 1) {
					selection = in.nextInt();
					if (selection == borrowerIDs.size() + 1) {
						return false;
					} else if (selection < 1 || selection > borrowerIDs.size() + 1) {
						return false;
					}
					admin.borrowerID = borrowerIDs.get(selection - 1);
					admin.borrowerName = borrowerNames.get(selection - 1);
					admin.borrowerAddress = borrowerAddrs.get(selection - 1);
					admin.borrowerPhone = borrowerPhones.get(selection - 1);
				}
				switch(operation){
				//add borrower name
					case 1: {
						changeItemDetail(borrowerOperation + operation);
						break;
					}
				//update borrower name
					case 2: {
						changeItemDetail(borrowerOperation + operation);
						break;
					}
				//delete borrower name
					case 3: {
						changeItemDetail(borrowerOperation + operation);
						break;
					}
				}
				break;
			}
			
			case 6 : {
				return false;
			}
			
		
		}
	
		return false;
	}
	
	boolean changeItemDetail(int action){

			switch (action) {
				//add book name
				case 1: {
					String newBookName = in.nextLine();
					while (true) {
						ResultSet rs = null;
						System.out.println("Please enter new book name: ");
						newBookName = in.nextLine();
						List<Object> para = new ArrayList<>();
						para.add(newBookName);
						try {
							rs = libSys.executeSQL(para, SQLCheckBkName);
							libSys.unholdExecuteSQL();
							if (rs.next()) {
								System.out.println("Book name is already in the system! Please re-enter: ");
								para.clear();
								continue;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						List<String> publisherNames = new ArrayList<>(), 
									 publisherIDs = new ArrayList<>(),
									 publisherAddrs = new ArrayList<>();
						int displayIndex = 1;
						try {
							rs = libSys.executeSQL(SQLShowAllPbs);
							libSys.unholdExecuteSQL();
							while (rs.next()) {
								String publisherID = rs.getString("publisherId");
								publisherIDs.add(publisherID);
								String publisherName = rs.getString("publisherName");
								publisherNames.add(publisherName);
								String publisherAddr = rs.getString("publisherAddress");
								publisherAddrs.add(publisherAddr);
								System.out.println(
										custTab + displayIndex++ + ") " + publisherName + " | " + publisherAddr);
							}
						} catch (SQLException e) {
							e.printStackTrace();
							return false;
						}
						System.out.println("Please selecet publisher: ");
						int selection = in.nextInt();
						if (selection == publisherIDs.size() + 1) {
							return false;
						} else if (selection < 1 || selection > publisherIDs.size() + 1) {
							return false;
						}
						List<String> data = new ArrayList<>();
						data.add(newBookName);
						data.add(publisherIDs.get(selection - 1));
						changeData(data, SQLInsertBkName);
						
						List<String> authorNames = new ArrayList<>(), 
									 authorIDs   = new ArrayList<>();
						displayIndex = 1;
						try {
							rs = libSys.executeSQL(SQLShowAllAus);
							libSys.unholdExecuteSQL();
							while (rs.next()) {
								String authorID = rs.getString("authorId");
								authorIDs.add(authorID);
								String authorName = rs.getString("authorName");
								authorNames.add(authorName);
								System.out.println(custTab + displayIndex++ + ") " + authorName);
							}
							System.out.println(custTab + displayIndex++ + ") Quit to previous");
						} catch (SQLException e) {
							e.printStackTrace();
							return false;
						}
						System.out.println("Please selecet author: ");
						selection = in.nextInt();
						if (selection == authorIDs.size() + 1) {
							return false;
						} else if (selection < 1 || selection > authorIDs.size() + 1) {
							return false;
						}
						para.clear();
						para.add(newBookName);
						String bookID = null;
						try {
							rs = libSys.executeSQL(para, SQLCheckBkName);
							libSys.unholdExecuteSQL();
							if (rs.next()) {
								bookID = rs.getString("bookId");
								para.clear();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						data.clear();
						data.add(bookID);
						data.add(authorIDs.get(selection - 1));
						changeData(data, SQLInsertBkAu);					
						break;
					}
					break;
				}
				//update book name
				case 2: {
					String updBookName = in.nextLine();
					while (true) {
						List<String> publisherNames = new ArrayList<>(), 
									 publisherIDs   = new ArrayList<>(),
									 publisherAddrs = new ArrayList<>();
						int selection = 0;
						System.out.println("Please enter new book name: ");
						ResultSet rs = null;
						updBookName = in.nextLine();
						List<Object> para = new ArrayList<>();
						para.add(updBookName);
						try {
							rs = libSys.executeSQL(para, SQLCheckBkName);
							libSys.unholdExecuteSQL();
							if (rs.next()) {
								System.out.println("Book name is already in the system! Please re-enter: ");
								para.clear();
								continue;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						System.out.println("Update this book's publisher? Y|N");
						String updateBoth = in.nextLine();
						if (updateBoth.equalsIgnoreCase("y")) {
							int displayIndex = 1;
							try {
								rs = libSys.executeSQL(SQLShowAllPbs);
								libSys.unholdExecuteSQL();
								while (rs.next()) {
									String publisherID = rs.getString("publisherId");
									publisherIDs.add(publisherID);
									String publisherName = rs.getString("publisherName");
									publisherNames.add(publisherName);
									String publisherAddr = rs.getString("publisherAddress");
									publisherAddrs.add(publisherAddr);
									System.out.println(
											custTab + displayIndex++ + ") " + publisherName + " | " + publisherAddr);
								}
							} catch (SQLException e) {
								e.printStackTrace();
								return false;
							}
							System.out.println("Please selecet publisher: ");
							selection = in.nextInt();
							if (selection == publisherIDs.size() + 1) {
								return false;
							} else if (selection < 1 || selection > publisherIDs.size() + 1) {
								return false;
							}
						}
						List<String> data = new ArrayList<>();
						if (updateBoth.equalsIgnoreCase("y")) {
							data.add(updBookName);
							data.add(publisherIDs.get(selection - 1));
							data.add(admin.bookID);
							changeData(data, SQLUpdateBkName2);
						} else {
							data.add(updBookName);
							data.add(admin.bookID);
							changeData(data, SQLUpdateBkName);
						}
						
						System.out.println("Update author name? Y/N");
						String updateAuthor = null;
						updateAuthor = in.next();
						if(updateAuthor.equalsIgnoreCase("n")){
							break;
						}
						
						List<String> authorNames = new ArrayList<>(), 
								 	 authorIDs   = new ArrayList<>();
						int displayIndex = 1;
						try {
							rs = libSys.executeSQL(SQLShowAllAus);
							libSys.unholdExecuteSQL();
							while (rs.next()) {
								String authorID = rs.getString("authorId");
								authorIDs.add(authorID);
								String authorName = rs.getString("authorName");
								authorNames.add(authorName);
								System.out.println(custTab + displayIndex++ + ") " + authorName);
							}
							System.out.println(custTab + displayIndex++ + ") Quit to previous");
						} catch (SQLException e) {
							e.printStackTrace();
							return false;
						}
						System.out.println("Please selecet author: ");
						selection = in.nextInt();
						if (selection == authorIDs.size() + 1) {
							return false;
						} else if (selection < 1 || selection > authorIDs.size() + 1) {
							return false;
						}
						para.clear();
						para.add(updBookName);
						String bookID = null;
						try {
							rs = libSys.executeSQL(para, SQLCheckBkName);
							libSys.unholdExecuteSQL();
							if (rs.next()) {
								bookID = rs.getString("bookId");
								para.clear();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						data.clear();
						data.add(authorIDs.get(selection - 1));
						data.add(bookID);
						changeData(data, SQLUpdateBkAu);	
						break;
					}
					break;
				}
				//delete book name
				case 3:{
					List<String> data = new ArrayList<>();
					data.add(admin.bookID);
					changeData(data, SQLDeleteBkName);
					break;
				}
				//add author name
				case 4: {
					String newAuName = in.nextLine();
					while (true) {
						System.out.println("Please enter new author name: ");
						newAuName = in.nextLine();
						List<Object> para = new ArrayList<>();
						ResultSet rs = null;
						para.add(newAuName);
						try {
							rs = libSys.executeSQL(para, SQLCheckAuName);
							libSys.unholdExecuteSQL();
							if (rs.next()) {
								System.out.println("Author name is already in the system! Please re-enter: ");
								para.clear();
								continue;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						List<String> data = new ArrayList<>();
						data.add(newAuName);
						changeData(data, SQLInsertAuName);
						break;
					}
					break;
				}
				//update author name
				case 5: {
					String updAuName = in.nextLine();
					while (true) {
						System.out.println("Please enter new author name: ");
						updAuName = in.nextLine();
						List<Object> para = new ArrayList<>();
						ResultSet rs = null;
						para.add(updAuName);
						try {
							rs = libSys.executeSQL(para, SQLCheckAuName);
							libSys.unholdExecuteSQL();
							if (rs.next()) {
								System.out.println("Author name is already in the system! Please re-enter: ");
								para.clear();
								continue;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						List<String> data = new ArrayList<>();
						data.add(updAuName);
						data.add(admin.authorID);
						changeData(data, SQLUpdateAuName);
						break;
					}
					break;
				}
				//delete author name
				case 6: {
					List<String> data = new ArrayList<>();
					data.add(admin.authorID);
					changeData(data, SQLDeleteAuName);
					break;
				}
				//add publisher name
				case 7: {
					String newPubName  = in.nextLine();
					while (true) {
						System.out.println("Please enter new publisher name: ");
						newPubName = in.nextLine();
						System.out.println("Please enter new publisher address: ");
						String newPubAddr = in.nextLine();
						System.out.println("Please enter new publisher phone: ");
						String newPubPhone = in.nextLine();
						List<Object> para = new ArrayList<>();
						ResultSet rs = null;
						para.add(newPubName);
						para.add(newPubAddr);
						try {
							rs = libSys.executeSQL(para, SQLCheckPbName);
							libSys.unholdExecuteSQL();
							if (rs.next()) {
								System.out.println("Publisher record is already in the system! Please re-enter: ");
								para.clear();
								continue;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						List<String> data = new ArrayList<>();
						data.add(newPubName);
						data.add(newPubAddr);
						data.add(newPubPhone);
						changeData(data, SQLInsertPbName);//!
						break;
					}
					break;
				}
				//update publisher name
				case 8: {
					String newPubName = in.nextLine();
					while (true) {
						System.out.println("Please enter new publisher name: ");
						newPubName = in.nextLine();
						System.out.println("Please enter new publisher addresss: ");
						String newPubAddr = in.nextLine();
						System.out.println("Please enter new publisher phone: ");
						String newPubPhone = in.nextLine();
						List<Object> para = new ArrayList<>();
						ResultSet rs = null;
						para.add(newPubName);
						para.add(newPubAddr);
						try {
							rs = libSys.executeSQL(para, SQLCheckPbName);
							libSys.unholdExecuteSQL();
							if (rs.next()) {
								System.out.println("Publisher record is already in the system! Please re-enter: ");
								para.clear();
								continue;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						List<String> data = new ArrayList<>();
						data.add(newPubName);
						data.add(newPubAddr);
						data.add(newPubPhone);
						data.add(admin.publisherID);
						changeData(data, SQLUpdatePbName);
						break;
					}
					break;
				}
				//delete publisher name
				case 9: {
					List<String> data = new ArrayList<>();
					data.add(admin.publisherID);
					changeData(data, SQLDeletePbName);
					break;
				}
				//add branch name
				case 10: {
					String newBchName = in.nextLine();
					while (true) {
						System.out.println("Please enter new branch name: ");
						newBchName = in.nextLine();
						System.out.println("Please enter new branch address: ");
						String newBchAddr = in.nextLine();
						List<Object> para = new ArrayList<>();
						ResultSet rs = null;
						para.add(newBchName);
						para.add(newBchAddr);
						try {
							rs = libSys.executeSQL(para, SQLCheckBchName);
							libSys.unholdExecuteSQL();
							if (rs.next()) {
								System.out.println("Library branch record is already in the system! Please re-enter: ");
								para.clear();
								continue;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						List<String> data = new ArrayList<>();
						data.add(newBchName);
						data.add(newBchAddr);
						changeData(data, SQLInsertBchName);
						break;
					}
					break;
				}
				//update branch name
				case 11: {
					String newBchName = in.nextLine();
					while (true) {
						System.out.println("Please enter new branch name: ");
						newBchName = in.nextLine();
						System.out.println("Please enter new branch addresss: ");
						String newBchAddr = in.nextLine();
						List<Object> para = new ArrayList<>();
						ResultSet rs = null;
						para.add(newBchName);
						para.add(newBchAddr);
						try {
							rs = libSys.executeSQL(para, SQLCheckBchName);
							libSys.unholdExecuteSQL();
							if (rs.next()) {
								System.out.println("Library branch record is already in the system! Please re-enter: ");
								para.clear();
								continue;
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						List<String> data = new ArrayList<>();
						data.add(newBchName);
						data.add(newBchAddr);
						data.add(admin.branchID);
						changeData(data, SQLUpdateBchName);
						break;
					}
					break;
				}
				//delete branch name
				case 12: {
					List<String> data = new ArrayList<>();
					data.add(admin.branchID);
					changeData(data, SQLDeleteBchName);
					break;
				}
				//add borrower name
				case 13: {
					System.out.println("Please enter new borrower name: ");
					String newBrwName  = in.nextLine();
					newBrwName  = in.nextLine();
					System.out.println("Please enter new borrower address: ");
					String newBrwAddr  = in.nextLine();
					System.out.println("Please enter new borrower phone: ");
					String newBrwPhone = in.nextLine();
					List<String> data = new ArrayList<>();
					data.add(newBrwName);
					data.add(newBrwAddr);
					data.add(newBrwPhone);
					changeData(data, SQLInsertBrwName);
					break;
				}
				//update borrower name
				case 14: {
					System.out.println("Please enter new borrower name: ");
					String newBrwName  = in.nextLine();
					newBrwName  = in.nextLine();
					System.out.println("Please enter new borrower address: ");
					String newBrwAddr  = in.nextLine();
					System.out.println("Please enter new borrower phone: ");
					String newBrwPhone = in.nextLine();
					List<String> data = new ArrayList<>();
					data.add(newBrwName);
					data.add(newBrwAddr);
					data.add(newBrwPhone);
					data.add(admin.borrowerID);
					changeData(data, SQLUpdateBrwName);
					break;
				}
				//delete borrower name
				case 15: {
					List<String> data = new ArrayList<>();
					data.add(admin.borrowerID);
					changeData(data, SQLDeleteBrwName);
					break;
				}
			}

		return true;
	}
	
	boolean changeDueDate(){
		ResultSet rs = null;
		int displayIndex = 1;
		List<String> bookIDs     = new ArrayList<>(), bookNames = new ArrayList<>(),
				 	 branchIDs   = new ArrayList<>(), branchNames   = new ArrayList<>(),
				 	 borrowerIDs = new ArrayList<>(), borrowerNames = new ArrayList<>(),
				 	 dateOuts    = new ArrayList<>(),
					 dateDues    = new ArrayList<>(),
					 dateIns     = new ArrayList<>();
		System.out.println("       Borrower | Book | Library Branch | Date Out| Due Date ");
		
		try {
			rs = libSys.executeSQL(SQLShowAllLoan);
			libSys.unholdExecuteSQL();
			while (rs.next()) {
				String bookID = rs.getString("bookId");
				bookIDs.add(bookID);
				String bookName = rs.getString("title");
				bookNames.add(bookName);
				String branchID = rs.getString("branchId");
				branchIDs.add(branchID);
				String branchName = rs.getString("branchName");
				branchNames.add(branchName);
				String borrowerID = rs.getString("cardNo");
				borrowerIDs.add(borrowerID);
				String borrowerName = rs.getString("name");
				borrowerNames.add(borrowerName);
				String dateOut = rs.getString("dateOut");
				dateOuts.add(dateOut);
				String dateDue = rs.getString("dueDate");
				dateDues.add(dateDue);
				String dateIn = rs.getString("dateIn");
				dateIns.add(dateIn);
				System.out.println(custTab + displayIndex++ + ") " 
						+ borrowerName + " | " + bookName + " | " + branchName + " | " + dateOut + " | "
						+ dateDue);
			}
			System.out.println(custTab + displayIndex++ + ") Quit to previous");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
			int selection = in.nextInt();
			String linePass = in.nextLine();
			if (selection == bookIDs.size() + 1) {
				return false;
			} else if (selection < 1 || selection > bookIDs.size() + 1) {
				return false;
			}
			admin.bookID     = bookIDs.get(selection - 1);
			admin.borrowerID = borrowerIDs.get(selection - 1);
			admin.branchID   = branchIDs.get(selection - 1);
			admin.dateDue    = dateDues.get(selection - 1);
			admin.dateOut    = dateOuts.get(selection-1);
			
			Date newDate = null;
			while (true) {
				Date date = new Date(System.currentTimeMillis());
				System.out.println("Please enter new due date as yyyy-mm-dd");
				System.out.println("For example: " + date);
				SimpleDateFormat dateInput = new SimpleDateFormat("yyyy-mm-dd");
				SimpleDateFormat dateInputOld = new SimpleDateFormat("yyyy-mm-dd");
				String newDueDate = in.nextLine();
				Date oldDate = null;
				try {
					newDate = new java.sql.Date(dateInput.parse(newDueDate).getTime());
					oldDate = new java.sql.Date(dateInputOld.parse(admin.dateOut.split(" ")[0]).getTime());
					//String tmp1 = newDate.toString();
					//String tmpo = admin.dateDue;
					//String tmp2 = oldDate.toString();
				} catch (ParseException e) {
					System.out.println("Invalid date input!");
					//e.printStackTrace();
					continue;
				}
				if (newDate.before(oldDate)) {
					System.out.println("Invalid due date!");
					continue;
				} else {
					break;
				} 
			}
			List<String> data = new ArrayList<>();
			data.add(newDate.toString());
			data.add(admin.bookID);
			data.add(admin.branchID);
			data.add(admin.borrowerID);
			changeData(data, SQLUpdateDueDate);
		return true;
	}
	
	void changeData(List<String> data, String SQL){
		List<Object> para = new ArrayList<>();
		para.addAll(data);
		try {
			libSys.executeUpdateSQL(para, SQL);
			libSys.unholdExecuteSQL();
			System.out.println("Success!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}

