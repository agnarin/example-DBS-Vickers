import java.math.BigDecimal;

/**
 * คลาสสำหรับจัดการบัญชีลูกค้า
 */
public class CustomerAccount {

    // 1. โครงสร้าง Balance
    private BigDecimal settledBalance;
    private BigDecimal earmarkedBalance;

    public CustomerAccount() {
        this.settledBalance = BigDecimal.ZERO;
        this.earmarkedBalance = BigDecimal.ZERO;
    }

    /**
     * คำนวณยอดที่ใช้ได้จริง (Available Balance)
     * Available = Settled - Earmarked
     */
    public BigDecimal getAvailableBalance() {
        return this.settledBalance.subtract(this.earmarkedBalance);
    }

    // --- ฟังก์ชันสำหรับการรับเงินเข้า (เช่น จากการขาย) ---
    public void creditSettledBalance(BigDecimal amount) {
        this.settledBalance = this.settledBalance.add(amount);
        System.out.println("Credit Settled: " + amount + ", New Settled: " + this.settledBalance);
    }
    
    // --- ฟังก์ชันสำหรับการตัดเงินจริง (เช่น ถึงเวลา 13:00) ---
    public void debitTransaction(BigDecimal amount) {
        // ลดทั้งยอด Settled (เงินจริงออก) และ Earmarked (ปลดล็อคการจอง)
        this.settledBalance = this.settledBalance.subtract(amount);
        this.earmarkedBalance = this.earmarkedBalance.subtract(amount);
        System.out.println("Debit Success: " + amount + ", New Settled: " + this.settledBalance + ", New Earmarked: " + this.earmarkedBalance);
    }


    /**
     * 2. ฟังก์ชัน Earmark เมื่อรับคำสั่งซื้อ (earmark)
     * * @param orderAmount จำนวนเงินที่สั่งซื้อ
     * @return true หาก Earmark สำเร็จ, false หากเงินไม่พอ
     */
    public boolean earmarkForPurchase(BigDecimal orderAmount) {
        BigDecimal available = getAvailableBalance();
        
        // ตรวจสอบว่าเงินที่ใช้ได้ (Available) > หรือไม่
        // (orderAmount <= available)
        if (orderAmount.compareTo(available) <= 0) {
            // มีเงินพอ, กันเงินไว้ (เพิ่มยอด Earmarked)
            this.earmarkedBalance = this.earmarkedBalance.add(orderAmount);
            System.out.println("Earmark Purchase OK: " + orderAmount + ". New Earmarked: " + this.earmarkedBalance);
            return true;
        } else {
            // เงินไม่พอ
            System.out.println("Earmark Purchase FAILED: Need " + orderAmount + ", Available " + available);
            return false;
        }
    }

    /**
     * 3. ฟังก์ชันสำหรับ "ตรวจสอบ" คำสั่งถอนเงิน (canWithdraw)
     * (ฟังก์ชันนี้ใช้แค่เช็ค ไม่กันเงิน)
     * * @param amount จำนวนเงินที่ต้องการถอน
     * @return true หากถอนได้
     */
    public boolean canWithdraw(BigDecimal amount) {
        BigDecimal available = getAvailableBalance();
        // (amount <= available)
        return amount.compareTo(available) <= 0;
    }

    /**
     * 3. ฟังก์ชันสำหรับ "จอง" คำสั่งถอนเงิน (reserveForWithdrawal)
     * (ฟังก์ชันนี้จะกันเงิน (Earmark) สำหรับการถอน)
     *
     * @param amount จำนวนเงินที่ต้องการถอน
     * @return true หากจองสำเร็จ
     */
    public boolean reserveForWithdrawal(BigDecimal amount) {
        if (canWithdraw(amount)) {
            // กันเงินส่วนนี้ไว้รอการโอนออกจริง
            this.earmarkedBalance = this.earmarkedBalance.add(amount);
            System.out.println("Reserve Withdrawal OK: " + amount + ". New Earmarked: " + this.earmarkedBalance);
            return true;
        } else {
            System.out.println("Reserve Withdrawal FAILED: Need " + amount + ", Available " + getAvailableBalance());
            return false;
        }
    }

    @Override
    public String toString() {
        return "AccountState [Settled=" + settledBalance + 
               ", Earmarked=" + earmarkedBalance + 
               ", AVAILABLE=" + getAvailableBalance() + "]";
    }
}