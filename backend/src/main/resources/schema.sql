CREATE OR REPLACE FUNCTION update_product_rating() RETURNS trigger AS '
DECLARE 
  new_rating   double precision;
  used_product_id varchar;
BEGIN 
  IF TG_OP = ''DELETE'' THEN
    used_product_id = OLD.produkt_id;
  ELSE 
    used_product_id = NEW.produkt_id;
  END IF;

  new_rating = (SELECT AVG(punkte) FROM rezension WHERE rezension.produkt_id = used_product_id);
  
  UPDATE produkt
  SET rating = new_rating
  WHERE produkt.produkt_id = used_product_id;

  IF TG_OP = ''DELETE'' THEN
    RETURN OLD;
  ELSE
    RETURN NEW;
  END IF;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER product_rating_update
AFTER INSERT OR UPDATE OR DELETE ON rezension
FOR EACH ROW
EXECUTE FUNCTION update_product_rating();