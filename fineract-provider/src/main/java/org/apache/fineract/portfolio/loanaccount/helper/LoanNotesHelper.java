/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 June 2023 at 23:55
 */
package org.apache.fineract.portfolio.loanaccount.helper;


import org.apache.fineract.infrastructure.bulkimport.constants.LoanConstants;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.note.domain.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.function.Consumer;

import java.util.List ;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


@Service
public class LoanNotesHelper{

    private NoteRepository noteRepository;
    private FromJsonHelper fromJsonHelper = new FromJsonHelper();

    @Autowired
    public LoanNotesHelper(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public void createNotesFromLoan(Loan loan , JsonCommand command){

        boolean hasNotes = command.hasParameter(LoanApiConstants.notesParam);

        List<Note> noteList = new ArrayList<>();

        if(hasNotes){
            JsonArray array = command.arrayOfParameterNamed(LoanApiConstants.notesParam);

            for(JsonElement jsonElement : array){
                String noteMesage = fromJsonHelper.extractStringNamed(LoanApiConstants.noteParamName ,jsonElement);
                System.err.println("--------------------notes is "+noteMesage);
                Note note = Note.loanNote(loan ,noteMesage);
                noteList.add(note);
            }
        }

        Consumer<Note> saveNotes = (e)-> noteRepository.saveAndFlush(e);
        noteList.stream().forEach(saveNotes);
    }
}
