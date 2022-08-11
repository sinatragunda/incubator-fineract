/*

    Created by Sinatra Gunda
    At 10:50 AM on 8/10/2022

*/
package org.apache.fineract.portfolio.paymentvoucher.repo;

import org.apache.fineract.portfolio.paymentvoucher.domain.PaymentVoucher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface PaymentVoucherRepository extends JpaRepository<PaymentVoucher, Long>, JpaSpecificationExecutor<PaymentVoucher>  {
}
