/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 March 2023 at 09:49
 */
package org.apache.fineract.presentation.screen.repo;

import org.apache.fineract.presentation.screen.domain.ScreenElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ScreenElementRepository extends JpaRepository<ScreenElement,Long> , JpaSpecificationExecutor<ScreenElement> {
}
