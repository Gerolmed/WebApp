package edu.hm.cs.projektstudium.findlunch.webapp.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Sets up information for the todo list of a sales person.
 */
@Entity
@Table(name = "swa_todo_list")
@Getter
@Setter
public class ToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    /**
     * The request type.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "todo_request_typ_id")
    private ToDoRequestTyp toDoRequestTyp;

    /**
     * The sales person.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "sales_person_id")
    private SalesPerson salesPerson;

    /**
     * The restaurant.
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;


    /**
     * The offer.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "offer_id")
    private Offer offer;


    /**
     * The date time.
     */
    @Type(type = "timestamp")
    private Date datetime;

    public ToDo() { super(); }
}
