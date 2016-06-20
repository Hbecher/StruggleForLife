(* LES TYPES *)

type action = 
|Action of int 
(* Action(0) correspond a la premiere action de la liste d'actions *)

type condition = 
|Decor of int
(* Decor(0) correspond a la condition : La case sur laquelle tu es corespond a l'element du decor 0 dans la liste des decors *)

type etat = int 
(* chaque etat de l'automates est represente par un entier *)

type transition = etat * condition * action * etat 
(* M. Perin *)

type automate = transition list
(* un automate se represente par une liste de transition M. Perin *) 


(* LES VARIABLES *)

let liste_decor =  ref  ([] : string list)
(* liste qui contiendra les decors a partir d'un fichier texte donne en argument du programme *)

let liste_action = ref  ([] : string list) 
(* liste qui contiendra les actions a partir d'un fichier texte donne en argument du programme *)


let action_int = ref (-1) 
(* reference qui contiendra l'action sous forme d'entier a placer dans le fichier xml *)

let nb_etat = ref 0 
(* reference le nb d'etat de l'automate a placer dans le fichier xml *)

let nom = ref ""
(* contiendra les noms des fichiers xml creer pour sauvegarder les automates : 1 fichier xml par automates *)

let nom_joueur = ref ""
(* contiendra les noms des joueurs *)

let automate_voulu = ref [] 
(* liste de transition sous forme " traduite " *)


let nb_aut = ref 0 
(* nb d'automate a creer *)

let choix = ref 0 
(* laisse le choix a l'utilisateur de creer son automate etat par etat ou automatiquement  *)

let liste_a_ajouter = ref( [] : int list)
 (* sert a la creation de la liste qui contiendra les choix possibles pour l autocompletion d'un etat de l'automate  *)

let liste_liste_alea = ref ([] : int list list) 
(* la liste qui contiendra les choix possibles pour l autocompletion d'un etat de l'automate *)

let etat_voulu = ref (0 : etat) 
(* pour l'autocompletion (remplir_etat i )*)

let action_voulu = ref (Action(0) : action) 
(* pour l'autocompletion (remplir_etat i )*)

let str_fich_source = Sys.argv.(1) 
(* ce programme prend en argument un fichier qui contient la liste des decors et des actions possible *)

(*
let nb_elem_decor = Sys.argv.(2) (* les arguments suivant sont le nb d'element de decor et le nb d'action non implemente *)
let nb_elem_action = Sys.argv.(3)
*)


(* FONCTIONS DE CONVERSION   *)

let (condition_to_int: condition -> int) =  function
  |Decor(i) -> i 

let (action_to_int: action -> int) = function
 |Action(i)-> i


let int_to_action (i : int) = match i with
  |i -> Action(i) 

let int_to_condition (i : int)  =  match i with
  |i ->Decor(i) 


let condition_to_string (c : condition) =  match c with
  | Decor(i)-> "La case ou vous etes est un(e) "^ (List.nth !liste_decor i)

let action_to_string (a : action)  = match a with 
  |Action(i)-> ( List.nth !liste_action i )

let  trans_to_string tt = match tt with 
  |(etat_int,condition_int,int_action,etat_suiv_int) -> string_of_int(etat_int)^","^ string_of_int(condition_int)^","^ string_of_int(int_action)^","^ string_of_int(etat_suiv_int)
(* on convertit la transition "traduite" en string *)

(*FONCTIONS DE TRADUCTION M. Perin *)


let (traduction_transition: transition -> int * int * int * int) = fun (src,condition,action,tgt) ->
   (src, condition_to_int condition, action_to_int action, tgt)

let (traduction_automate: automate -> (int * int * int * int) list) = fun automate ->
   List.map traduction_transition  automate (*(fst automate)*) ;;


(* FONCTIONS D'AFFICHAGE  *)


let print_condition (c : condition) = match c with
  |Decor(i) -> print_endline (condition_to_string c);
;;

let afficher_action x = 
  print_endline "Quelle action voulez-vous faire :";
  for i = 0 to ( (List.length !liste_action) - 1 ) 
  do
    print_int(i);
    print_endline (" "^List.nth !liste_action i);
  done
    
(*affichage d'une liste d entier debug  *)
let rec print_list (l:int list) = match l with
  |[] -> ()
  |e::f -> print_int e; print_string " "; print_list f;

;;

(*affichage d'une liste de liste d'entier debug  *)
let rec print_list_list (l:int list list) = match l with 
    []-> ()
  |l1::suite-> print_list l1 ;  print_string ";" ;print_list_list suite ;

;;

(* FONCTIONS D'AUTOCOMPLETION *)

(* a commenter *)

let creer_liste_liste_alea n s =
  begin
    let k = ref 0 in ();
    for i = (List.length (!liste_decor)) -1  downto 0
   
 
    do 
      liste_a_ajouter := [];
      for j = (s-1) downto 0 
      do
	liste_a_ajouter := j::(!liste_a_ajouter);
      done;
      if (i >= s) then
	k:= 0;
      while !k <= (n-1)(* *)
      do
	liste_a_ajouter := ( n*i+(s-1) - !k )::(!liste_a_ajouter);
	k := !k + s;
      done;

	liste_liste_alea := !liste_a_ajouter::!liste_liste_alea;
      

      done
  end

(* remplissage pseudo_aleatoire d'un etat *)
    
let remplir_etat i =
  for l = 0 to ( (List.length !liste_decor) - 1 )
  do
    etat_voulu :=( (i + 1) mod !nb_etat);
    let li =(List.nth (!liste_liste_alea) l) in ();
    let rand = Random.int (List.length li) in ();
    let int_action = (List.nth (li) rand) in ();
    automate_voulu := (!automate_voulu)@ [( i ,l,int_action,(!etat_voulu))];

  done
    

(*   FICHIER XML   *)

(* on verifie que le nom ne contient que des minuscules renvoi vrai si erreur  *)

let rec verif_nom (s:string) = match s with
    ""-> false
  |s-> (s.[0])>'z' || (s.[0])<'a' || verif_nom (String.sub s 1 ((String.length s)-1) )



(*a commenter *)
let choix_nom x =  
  begin
    print_endline "Donner votre nom :";
    let _ = nom_joueur := read_line() in
    while ( (String.length !nom_joueur) < 3 ||(String.length !nom_joueur) > 10 
	    || verif_nom !nom_joueur 
	   (* || Sys.file_exists ( !nom^"V"^ (string_of_int (List.length !liste_decor)^".xml") ) *)  )
    do
      print_endline "erreur : votre nom est incorrect : Il doit comporter de 3 a 10 caracteres minuscules sans espaces et ne doit pas deja exister\n"; 
      nom_joueur := read_line() ;
    done; 
    nom := !nom_joueur^"V"^ (string_of_int (List.length !liste_decor) )^".xml";
    print_string( "nom du fichier : "^(!nom) );
    print_newline();
  end

(* a commenter *)


(* on écrit les elements necessaire dans un fichier xml pour la creation des automates par le programme java *)
let ecriture_xml i = 
  begin
    let fichier1 = open_out !nom in ();  (* on ouvre le fichier de sauvegarde xml *)
    output_string fichier1 "<?xml version = \"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n";
    output_string fichier1 "<automate>\n";
    output_string fichier1 "<nom>";
    output_string fichier1 !nom_joueur;
    output_string fichier1 "</nom>\n";
    output_string fichier1 "<nb_symbole>";
    output_string fichier1 (string_of_int ( List.length !liste_decor ) ) ;
    output_string fichier1 "</nb_symbole>\n";
    output_string fichier1 "<nb_etat>";
    output_string fichier1 (string_of_int (!nb_etat) ); 
    output_string fichier1 "</nb_etat>\n";
    output_string fichier1 "<transitions>\n";
    output_string fichier1 "<!-- Etat_in,symbole,action,Etat_out -->\n";

    for j = 0 to (!nb_etat * List.length !liste_decor) -1  (* nb_elem_decor *)
    do 
      output_string fichier1 "<transition>"; 
      output_string fichier1 (trans_to_string (List.nth (!automate_voulu) j) );
      output_string fichier1 "</transition>\n";
    done;
    output_string fichier1 "</transitions>\n";
    output_string fichier1 "</automate>";
    close_out fichier1; 
  end



let choix_etat x = 
  begin
    nb_etat := 0 ;
    while(!nb_etat <= 0 || !nb_etat > 128)
    do
      print_endline "Donnez le nombre d'etats de votre automate entre 1 et 128 :";
      nb_etat :=  read_int() ;
    done

  end  

(* FONCTION PRINCIPALE *)

(* a commenter *)
let creer_aut x  =  
  begin

    choix_nom x;
    choix_etat x;
   
    
    for i = 0 to !nb_etat -1
    do
      print_endline "Voulez vous une completion automatique de l'etat suivant de votre automate 0 : OUI 1 : NON ? ";
      choix := read_int();
      match !choix with
      |0->remplir_etat i;
      |_->
	begin
	  (* print_int !choix; *)
	  print_string  "Si vous etes dans l'etat "; print_int i (* !i *) ; print_newline() ;
 

	  for j = 0 to (List.length !liste_decor) -1 
	  do
	    print_string "et si la condition suivante est verifiee : ";
	
	    print_condition (int_to_condition j);
	    action_int := (-1); (* *)
	    while (!action_int < 0 || !action_int >= (List.length !liste_action) ) (* nb_elem_action ?*)
	    do
	      afficher_action x ;
	      action_int := read_int();
	    done;
	    etat_voulu := (-1);
	    while( !etat_voulu < 0 || !etat_voulu >= !nb_etat)
	    do
	      print_endline "Dans quelle etat voulez vous aller apres avoir effectue cette action ?";
	      etat_voulu := read_int(); (* read_etat ? *)
	    done;
	   
	  (* etat i condition to int j  action_voulu,etat_voulu *)
	    automate_voulu := (!automate_voulu)@ [ (i ,j,(!action_int),(!etat_voulu))];
	  done;
	
   
	end;

    done;
  (* ecriture de l'automate dans un fichier *)
  ecriture_xml x;
 

  end


(* MAIN *)
(* comment ? *)
let main = 
  begin 
      Random.self_init(); (*on initialise l'aleatoire *)

    let fichier_source = open_in str_fich_source in (); (*on ouvre en lecture le fichier qui contient les
							liste des decors et actions (argv[1]) pour pouvoir
							  remplir les listes ci dessous *)


 (*remplissage des liste a partir d'un fichier de config du jeu *)
    print_endline "Les decors seront les suivants :";
    for i = 0 to 3 (* attention Sys.argv.(2)] *)
    do
      liste_decor := !liste_decor@[input_line fichier_source];
      print_endline (List.nth !liste_decor i);
    done;

    let _ = input_line fichier_source (* saut de ligne *) in ();
    (* selon le format du fichier source a enlever *)
    print_endline "Les actions seront les suivantes :";
    for i = 0 to 6 (* attention Sys.argv.(3) *)
    do
      liste_action := !liste_action@[input_line fichier_source];
       print_endline (List.nth !liste_action i); 
    done;

    creer_liste_liste_alea 2 1; (*attention ne marche pas encore si nb de symbole spec ( 2eme argument ) diff de 1 *)
    (* print_list_list !liste_liste_alea; (* affichage liste debug  *) *)

    while (!nb_aut <= 0 )
    do
      print_endline "Donnez le nombre d'automates a creer : ";
      nb_aut := read_int();      
    done;

    for j = 0 to (!nb_aut -1)
    do
      automate_voulu :=  [] ; (* on reinitialise la variable qui contiadra l'automate ous forme traduite *)   
      creer_aut j;


   done; 

 end 

