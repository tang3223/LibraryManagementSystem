import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

class Librarian{
	
	String branchName, branchID, branchAddr;
	String bookID, bookName;
	
}

class LibrarianProcedure {
	
	/**********************************************/
	String SQLReadBranches  = "SELECT * FROM tbl_library_branch lb;",
		   SQLReadBooks     = "SELECT * FROM tbl_book_copies bc JOIN (tbl_book b, tbl_library_branch lb) "
		   		+ "ON (b.bookId=bc.bookId AND lb.branchId=bc.branchId) WHERE lb.branchName=? AND bc.noOfCopies > 0;",
		   SQLAddCheckOut   = "INSERT INTO tbl_book_loans (bookId, branchId, cardNo, dateOut, dueDate);"
		   		+ "VALUE (?,?,?,CURDATE(),DATE_ADD(CURDATE(),INTERVAL 7 DAY));",
		   SQLIDCheck       = "SELECT * FROM tbl_borrower br WHERE br.cardNo=?;",
		   SQLBorrowedBook  = "SELECT * FROM tbl_book_loans bl JOIN tbl_book b ON (bl.bookId=b.bookId) WHERE bl.dateIn IS NULL AND bl.cardNo=?;",
		   SQLReturnBook    = "UPDATE tbl_book_loans bl SET bl.dateIn=CURDATE() WHERE bl.bookId=?",
		   SQLUpdBranchName = "UPDATE tbl_library_branch lb SET lb.branchName=? WHERE lb.branchId=?",
		   SQLUpdBranchAddr = "UPDATE tbl_library_branch lb SET lb.branchAddress=? WHERE lb.branchId=?",
		   SQLRdAllBooks    = "SELECT * FROM tbl_book_copies bc JOIN (tbl_book b, tbl_library_branch lb) "
		   		+ "ON (b.bookId=bc.bookId AND lb.branchId=bc.branchId) WHERE lb.branchId=?",
		   SQLRdBookCopies  = "SELECT * FROM tbl_book_copies bc WHERE bc.bookId=?",
		   SQLUpdBookCopies = "UPDATE tbl_book_copies bc SET bc.noOfCopies=? WHERE bc.bookId=? AND bc.branchId=?";
	
	/**********************************************/	
	
	Scanner in = new Scanner(System.in);
	LibSystem libSys    = new LibSystem();
	String custTab      = "    ";
	Librarian librarian = new Librarian();
	
	boolean execute(){
		do {
			System.out.println("1) Enter branch you manage");
			System.out.println("2) Quit to previous");
			int selection =in.nextInt();
			if (selection == 2) {
				return false;
			} else if (selection == 1) {
				if (!manageBranch()) {
					continue;
				}
				else {
					break;
				}
			} else {
				return false;
			} 
		} while (true);
		return true;
	}
	
	boolean manageBranch(){
		do {
			ResultSet rs = null;
			List<String> branchNames = new ArrayList<>(), 
					     branchAddrs = new ArrayList<>(),
					     branchIDs   = new ArrayList<>();
			int displayIndex = 1;
			try {
				rs = libSys.executeSQL(SQLReadBranches);
				libSys.unholdExecuteSQL();
				while (rs.next()) {
					String branchID   = rs.getString("branchId");
					branchIDs.add(branchID);
					String branchName = rs.getString("branchName");
					branchNames.add(branchName);
					String branchAddr = rs.getString("branchAddress");
					branchAddrs.add(branchAddr);
					System.out.println(custTab + displayIndex++ + ") " + branchName + ", " + branchAddr);
				}
				System.out.println(custTab + displayIndex++ + ") Quit to previous");
			} catch (SQLException e) {
				e.printStackTrace();
			}
			int selection = in.nextInt();
			if (selection == branchIDs.size() + 1) {
				return false;
			} else if (selection < 1 || selection > branchIDs.size() + 1) {
				return false;
			}
			librarian.branchID = branchIDs.get(selection - 1);
			librarian.branchName = branchNames.get(selection - 1);
			if (!selectAction()) {
				continue;
			}
			else {
				break;
			}
		} while (true);
		return true;
	}
	
	boolean selectAction(){
		do {
			System.out.println("\n1) Update the details of the Library");
			System.out.println("2) Add copies of Book to the Branch");
			System.out.println("3) Quit to previous");
			int selection = in.nextInt();
			switch (selection) {
			case 1: {
				if(!updateBranch()){
					continue;
				}
				else {
					return true;				
				}
			}
			case 2: {
				if (!changeCopies()){
					continue;
				}
				else {
					return true;
				}
			}
			case 3: {
				return false;
			}
			}
		} while (true);
	}
	
	boolean updateBranch(){
		String updateBranchName = in.nextLine();
		List<Object> para = new ArrayList<>();
		System.out.println("You have chosen to update the Branch with Branch Id: "
				+ librarian.branchID + " and Branch Name: " + librarian.branchName + ".");
		System.out.println("Enter \"quit\" at any prompt to cancel operation.\n");
		System.out.println("Please enter new branch name or enter N/A for no change: ");
		updateBranchName = in.nextLine();
		if (checkQuit(updateBranchName)) {
			return false;
		}
		else if (updateBranchName.trim().equalsIgnoreCase("N/A")) {}
		else {
			librarian.branchName = updateBranchName;
			para.add(librarian.branchName);
			para.add(librarian.branchID);
			
			try {
				libSys.executeUpdateSQL(para, SQLUpdBranchName);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		para.clear();
		System.out.println("Please enter new branch address or enter N/A for no change: ");
		String updateBranchAddr = in.nextLine();
		if (checkQuit(updateBranchAddr)) {
			return false;
		}
		else if (updateBranchAddr.trim().equalsIgnoreCase("N/A")) {}
		else {
			librarian.branchAddr = updateBranchAddr;
			para.add(librarian.branchAddr);
			para.add(librarian.branchID);
			
			try {
				libSys.executeUpdateSQL(para, SQLUpdBranchAddr);
				libSys.unholdExecuteSQL();
				System.out.println("Successfully updated!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	boolean checkQuit(String in){
		if (in.trim().equalsIgnoreCase("quit")){
			return true;
		}
		return false;
	}
	
	boolean changeCopies(){
		int displayIndex = 1;
		ResultSet rs = null;
		List<Object> para      = new ArrayList<>();
		List<String> bookNames = new ArrayList<>(),
			         bookIDs   = new ArrayList<>();
		System.out.println("\nPick the book you want to add copies of, to your branch:");
		para.add(librarian.branchID);
		try {
			rs = libSys.executeSQL(para,SQLRdAllBooks);
			libSys.unholdExecuteSQL();
			while(rs.next()){
				String bookID   = rs.getString("bookId");
				bookIDs.add(bookID);
				String bookName = rs.getString("title");
				bookNames.add(bookName);
				System.out.println(custTab + displayIndex++ + ") " + bookName); 
			}
			System.out.println(custTab + displayIndex++ + ") Quit to cancel operation");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int selection = in.nextInt();
		if (selection == bookIDs.size()+1) {
			return false;
		}
		else if (selection < 1 || selection > bookIDs.size()+1){
			return false;
		}
		librarian.bookID   = bookIDs.get(selection-1);
		librarian.bookName = bookNames.get(selection-1);
		if (changeNumOfCopies()){
			return false;
		}
		return true;
	}
	
	boolean changeNumOfCopies(){
		ResultSet rs = null;
		List<Object> para = new ArrayList<>();
		para.add(librarian.bookID);
		try {
			rs = libSys.executeSQL(para,SQLRdBookCopies);
			libSys.unholdExecuteSQL();
			if(rs.next()){
				while (true) {
					String bookID     = rs.getString("bookId");
					String branchID   = rs.getString("branchId");
					String bookCopies = rs.getString("noOfCopies");
					System.out.println("\nExisting number of copies: " + bookCopies);
					System.out.println("Enter new number of copies: ");
					int updatedCopies = in.nextInt();
					if (updatedCopies < 0) {
						System.out.println("Invlid number of copies! Please re-enter: ");
						continue;
					}
					String updatedBookCopies = String.valueOf(updatedCopies);
					para.clear();
					para.add(updatedBookCopies);
					para.add(bookID);
					para.add(branchID);
					libSys.executeUpdateSQL(para, SQLUpdBookCopies);
					libSys.unholdExecuteSQL();
					System.out.println("Successfully updated!");
					break;
				}
			}
		} catch (SQLException e) {			
			e.printStackTrace();
			return false;
		}
		return true;
	}
		
	
	
	
}
