/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 07 March 2023 at 02:39
 */
package org.apache.fineract.infrastructure.dataqueries.repo;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.dataqueries.domain.Report;
import org.apache.fineract.infrastructure.dataqueries.domain.ReportRepository;
import org.apache.fineract.infrastructure.dataqueries.domain.XRegisteredTable;
import org.apache.fineract.infrastructure.dataqueries.exception.DatatableNotFoundException;
import org.apache.fineract.infrastructure.dataqueries.exception.ReportNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  A wrapper class for the ReportRepository that provides a method that returns a Report entity if it exists,
 *  else throws "ReportNotFoundException" exception if the Report does not exist
 **/
@Service
public class XRegisteredTableRepositoryWrapper {

    private final XRegisteredTableRepository xRegisteredTableRepository;

    @Autowired
    public XRegisteredTableRepositoryWrapper(XRegisteredTableRepository xRegisteredTableRepository) {
        this.xRegisteredTableRepository = xRegisteredTableRepository;
    }

    public XRegisteredTable findOneWithNotFoundDetection(final Long id) {

        final XRegisteredTable xRegisteredTable = this.xRegisteredTableRepository.findOne(id);

        if (xRegisteredTable== null) {
            throw new DatatableNotFoundException("" ,id);
        }
        return xRegisteredTable;
    }


    public XRegisteredTable findByRegisteredTableNameWithNotFoundDetection(final String tableName) {

        final XRegisteredTable xRegisteredTable = this.xRegisteredTableRepository.findByRegisteredTableName(tableName);
        if (xRegisteredTable== null) {
            throw new DatatableNotFoundException(tableName ,null);
        }
        return xRegisteredTable;
    }

    public XRegisteredTable findByRegisteredTableName(final String tableName) {

        final XRegisteredTable xRegisteredTable = this.xRegisteredTableRepository.findByRegisteredTableName(tableName);
        return xRegisteredTable;
    }

    public boolean hasTable(String tableName){
        XRegisteredTable table = findByRegisteredTableName(tableName);
        return OptionalHelper.isPresent(table);
    }
}
