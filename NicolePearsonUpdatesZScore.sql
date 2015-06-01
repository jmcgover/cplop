
DROP PROCEDURE IF EXISTS matchIsolatesNicoleZScore;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `matchIsolatesNicoleZScore`(IN isoID1 VARCHAR(10), IN isoID2 VARCHAR(10), IN region VARCHAR(10), IN length INT(10),   IN includeErroneous BOOL, OUT score double)
BEGIN

   IF region = '23-5' THEN
      SET length = 93;
   END IF;
   IF region = '16-23' THEN
      SET length = 95;
   END IF;


    SELECT AVG(PearsonSmart(a.pyroID, b.pyroID, length)) INTO score
	FROM Pyroprints a, Pyroprints b 
	WHERE a.isoID = isoID1 AND b.isoID = isoID2 AND a.appliedRegion = region AND b.appliedRegion = region AND (includeErroneous or NOT a.isErroneous) AND (includeErroneous or NOT b.isErroneous);

    IF score IS NULL THEN
      SET score = 0.0;
    END IF;

END ;;
DELIMITER ;

DROP FUNCTION IF EXISTS matchIsolatePairsNicoleZScore;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `matchIsolatePairsNicoleZScore`(isoID1 VARCHAR(10), isoID2 VARCHAR(10), region VARCHAR(10), length INT(10),            includeErroneous BOOL) RETURNS double
BEGIN
  DECLARE score DOUBLE DEFAULT 0.0;
  CALL matchIsolatesNicoleZScore(isoID1, isoID2, region, length, includeErroneous, score);
  RETURN score;
END ;;
DELIMITER ;
