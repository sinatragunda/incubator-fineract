/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 04 January 2023 at 11:02
 */
package org.apache.fineract.accounting.journalentry.domain;
import org.apache.fineract.accounting.journalentry.exception.JournalEntriesNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class JournalEntryRepositoryWrapper {

    private JournalEntryRepository journalEntryRepository;


    @Autowired
    public JournalEntryRepositoryWrapper(JournalEntryRepository journalEntryRepository){
        this.journalEntryRepository = journalEntryRepository;
    }

    public void update(JournalEntry ...entry){
        Arrays.stream(entry).forEach(e->{
            System.err.println("---------------update account with id "+e.getId());
            journalEntryRepository.saveAndFlush(e);
        });
    }

    public void reverseEntry(JournalEntry ...entry){
        Arrays.stream(entry).forEach(e->{
            e.setReversed(true);
            //System.err.println("---------------reverse account with id "+e.isReversed());
            journalEntryRepository.saveAndFlush(e);
        });   
    }

    public JournalEntry findOneWithNotFoundDetection(Long id){
        JournalEntry journalEntry = journalEntryRepository.findOne(id);
        boolean isPresent = Optional.ofNullable(journalEntry).isPresent();
        if(!isPresent){
            /// throw some exception
        }
        return journalEntry ;
    }
}
