/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 13:23
 */
package org.apache.fineract.infrastructure.documentmanagement.repo;


import org.apache.fineract.infrastructure.documentmanagement.domain.Document;
import org.apache.fineract.infrastructure.documentmanagement.domain.DocumentRepository;
import org.apache.fineract.infrastructure.documentmanagement.exception.ContentManagementException;
import org.apache.fineract.infrastructure.documentmanagement.exception.DocumentNotFoundException;
import org.apache.fineract.utility.helper.JpaHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DocumentRepositoryWrapper {

    private DocumentRepository documentRepository;

    @Autowired
    public DocumentRepositoryWrapper(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document findOneWithNotFoundDetection(Long id){
        return JpaHelper.findOneWithNotFoundDetection(documentRepository ,id ,new ContentManagementException());
    }
}
