package unicam.hackHub.entities.team;

import unicam.hackHub.entities.utente.Utente;

import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;

@Entity
public class Team {

    @Id
    private String id;

    private String nome;

    //Lista membri del Team
    @OneToMany
    private List<Utente> membri = new ArrayList<>();

    //Lista inviti Spediti
    @OneToMany(mappedBy = "mittente")
    private List<Invito> invitiSpediti = new ArrayList<>();

    //costruttore vuoto per leggere i dati dal db
    public Team(){ }

    //costruttore
    public Team(String id, String nome){
        this.id=id;
        this.nome=nome;
    }

    public void aggiungiMembro(Utente utente) {
        this.membri.add(utente);
    }

    public void rimuoviMembro(Utente utente) {
        this.membri.remove(utente);
    }

    public Invito creaInvito(Utente utente) {
        String idTemp = java.util.UUID.randomUUID().toString();

        Invito nuovoInvito = new Invito(
                idTemp,
                new Date(),
                StatoInvito.PENDENTE,
                this,
                utente
        );

        this.invitiSpediti.add(nuovoInvito);
        return nuovoInvito;
    }

    // --- Getter e Setter ---

    public String getId(){
        return id;
    }

    public List<Utente> getMembri() {
        return List.copyOf(membri);
    }

    public String setId(String id){
       return this.id=id;
    }

    public String getNome(){
        return nome;
    }

    public String setNome(String nome){
        return this.nome=nome;
    }
}
