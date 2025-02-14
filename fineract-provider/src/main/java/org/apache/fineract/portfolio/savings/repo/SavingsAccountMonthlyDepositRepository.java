/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


// Created 20/07/2021 at 1513

package org.apache.fineract.portfolio.savings.repo;

import java.util.Date;
import java.util.List;

import org.apache.fineract.portfolio.savings.domain.SavingsAccountMonthlyDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// Added 22/07/2021

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SavingsAccountMonthlyDepositRepository extends JpaRepository<SavingsAccountMonthlyDeposit, Long>,
        JpaSpecificationExecutor<SavingsAccountMonthlyDeposit> {

    SavingsAccountMonthlyDeposit findOneByStartDate(Date startDate);
    SavingsAccountMonthlyDeposit findOneBySavingsAccountIdAndStartDate(Long id ,Date startDate);
    List<SavingsAccountMonthlyDeposit> findBySavingsAccountId(Long id);

    @Query("Select a from SavingsAccountMonthlyDeposit a where a.startDate between :startDate and :endDate")
    List<SavingsAccountMonthlyDeposit> findByDatesInBetween(@Param("startDate")Date startDate ,@Param("endDate")Date endDate);

    @Query("Select a from SavingsAccountMonthlyDeposit a where a.startDate between :startDate and :endDate and a.savingsAccountId = :id")
    List<SavingsAccountMonthlyDeposit> findByDatesInBetweenAndAccountId(@Param("startDate")Date startDate ,@Param("endDate")Date endDate ,@Param("id")Long id);

}