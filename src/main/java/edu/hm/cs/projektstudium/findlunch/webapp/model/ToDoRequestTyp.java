package edu.hm.cs.projektstudium.findlunch.webapp.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


/**
 * The date time.
 */
@Entity
@Table(name = "swa_todo_request_typ")
@Getter
@Setter
public class ToDoRequestTyp {

	/**
	 * The id.
	 */
    @Id
    private int id;


    /**
     * Type of the todo request.
     */
    private String todoRequestTyp;

    /**
     * List of todo's.
     */
    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY)
    private List<ToDo> toDos;

    public ToDoRequestTyp() { super(); }
}
