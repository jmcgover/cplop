USE CPLOP;

-- Checks if comparison between two pyroprints would make sense. Currently, it
-- only verifies that they have the same appliedRegion to stay equivalent to
-- old implementations. But it should probably be expanded to include things
-- like dispensation sequences.
-- Returns TRUE if comparison would be valid, FALSE otherwise.
DROP FUNCTION IF EXISTS CheckPyroprintCompatibility;
DELIMITER ;;
CREATE FUNCTION CheckPyroprintCompatibility (pyro1 INT, pyro2 INT) RETURNS BOOLEAN DETERMINISTIC
BEGIN
	DECLARE same_region BOOLEAN DEFAULT 0;
	
	SELECT a.appliedRegion = b.appliedRegion INTO same_region
		FROM Pyroprints a, Pyroprints b
		WHERE a.pyroID = pyro1 AND b.pyroID = pyro2;

	RETURN(same_region);
END ;;
DELIMITER ;


-- Checks if comparison of the zScores of two pyroprints would make sense.
-- Pearson correlation requires that the zScores were precomputed with the
-- correct number of dispensations.
-- Assumes CheckPyroprintCompatibility(pyro1, pyro2) = TRUE
-- Returns the number of zScores if comparison would be valid and the value
-- 0 otherwise.
DROP FUNCTION IF EXISTS GetCompatibleZScoreLength;
DELIMITER ;;
CREATE FUNCTION GetCompatibleZScoreLength (pyro1 INT, pyro2 INT) RETURNS INT DETERMINISTIC
BEGIN
	DECLARE r_length INT DEFAULT 0;
	DECLARE a_length INT DEFAULT 0;
	DECLARE b_length INT DEFAULT 0;
	
	SELECT pearsonDispLength INTO r_length
		FROM Regions JOIN Pyroprints USING(appliedRegion)
		WHERE pyroID = pyro1;
	SELECT COUNT(*) INTO a_length FROM ZScores WHERE pyroID = pyro1;
	SELECT COUNT(*) INTO b_length FROM ZScores WHERE pyroID = pyro2;

	IF r_length = a_length AND r_length = b_length THEN
		RETURN (r_length);
	ELSE
		RETURN (0);
	END IF;
END ;;
DELIMITER ;


-- Alternate implementation of standard PearsonMatch. 2x as fast as old
-- implementation because it uses aggregate functions for avg and std deviation.
-- Returns negative value if called with pyroprints of different regions.
DROP FUNCTION IF EXISTS PearsonGeneral;
DELIMITER ;;
CREATE FUNCTION PearsonGeneral (pyro1 INT, pyro2 INT, length INT) RETURNS float DETERMINISTIC
BEGIN
	DECLARE sum FLOAT DEFAULT 0;
	DECLARE mean1, mean2, stdDev1, stdDev2 FLOAT DEFAULT NULL;
	DECLARE a_length INT DEFAULT 0;
	DECLARE b_length INT DEFAULT 0;
	
	IF NOT CheckPyroprintCompatibility(pyro1, pyro2) THEN
		RETURN -2;
	END IF;

	-- This is what the old implementation does. I'd rather it just returned
	-- an error if they aren't at least as long as length, instead of silently
	-- changing the symantics of Pearson correlation. If changed to error,
	-- maybe move the length check into CheckPyroprintCompatibility().
	SELECT COUNT(*) INTO a_length FROM Histograms WHERE pyroID = pyro1;
	SELECT COUNT(*) INTO b_length FROM Histograms WHERE pyroID = pyro2;
	SET length = LEAST(length, a_length, b_length); -- I don't like this.

	-- 2x speed up from using aggregate functions for mean and stdDev.
	select avg(pHeight), std(pHeight) INTO mean1, stdDev1 FROM Histograms
			WHERE pyroID = pyro1 AND position < length;
	select avg(pHeight), std(pHeight) INTO mean2, stdDev2 FROM Histograms
			WHERE pyroID = pyro2 AND position < length;
 
	SELECT SUM((a.pHeight - mean1) * (b.pHeight - mean2)) INTO sum
		FROM Histograms a, Histograms b
		WHERE a.pyroID = pyro1 AND b.pyroID = pyro2 
			AND a.position = b.position
			AND a.position < length;

	RETURN(sum / (length * stdDev1 * stdDev2));
END ;;
DELIMITER ;


-- Alternate implementation of PearsonMatch calculated as the dot product of
-- the zScore vectors of two pyroprints. 6x as fast as old PearsonMatch. 3x as
-- fast as PearsonGeneral.
-- Same as PearsonMatch(pyro1, pyro2, Regions.pearsonDispLength)
-- Returns negative value on error.
DROP FUNCTION IF EXISTS PearsonZScore;
DELIMITER ;;
CREATE FUNCTION PearsonZScore (pyro1 INT, pyro2 INT) RETURNS float DETERMINISTIC
BEGIN
	DECLARE pearson FLOAT DEFAULT -1;
	DECLARE z_length INT DEFAULT 0;
	
	IF NOT CheckPyroprintCompatibility(pyro1, pyro2) THEN
		RETURN -2;
	END IF;

	SET z_length = GetCompatibleZScoreLength(pyro1, pyro2);
	IF z_length <= 0 THEN
		RETURN -3;
	END IF;
 
	SELECT SUM(a.zHeight * b.zHeight)/z_length INTO pearson
		FROM ZScores a, ZScores b
		WHERE a.pyroID = pyro1 AND b.pyroID = pyro2 
			AND a.position = b.position
			AND a.position < z_length; 

	RETURN(pearson);
END ;;
DELIMITER ;

-- Uses PearsonZScore when length is correct, otherwise it uses PearsonGeneral.
-- Returns negative value if called with pyroprints of different regions.
DROP FUNCTION IF EXISTS PearsonSmart;
DELIMITER ;;
CREATE FUNCTION PearsonSmart (pyro1 INT, pyro2 INT, length INT) RETURNS float DETERMINISTIC
BEGIN
	DECLARE z_length INT DEFAULT 0;
	
	IF NOT CheckPyroprintCompatibility(pyro1, pyro2) THEN
		RETURN -2;
	END IF;

	SET z_length = GetCompatibleZScoreLength(pyro1, pyro2);

	IF z_length <= 0 OR length <> z_length THEN
		RETURN(PearsonGeneral(pyro1, pyro2, length));
	ELSE
		RETURN(PearsonZScore(pyro1, pyro2));
	END IF;
END ;;
DELIMITER ;

-- Computes the ZScores for a single pyroprint and adds them to ZScores.
DROP PROCEDURE IF EXISTS ComputeZScores;
DELIMITER ;;
CREATE PROCEDURE ComputeZScores (pyro INT)
BEGIN
	DECLARE pearson FLOAT DEFAULT -2;
	DECLARE avgHeight, stdHeight, avgWidth, stdWidth, avgArea, stdArea DOUBLE DEFAULT 0;

	SELECT AVG(pHeight), STD(pHeight),
			AVG(PeakWidth), STD(PeakWidth),
			AVG(PeakArea), STD(PeakArea)
		INTO avgHeight, stdHeight, avgWidth, stdWidth, avgArea, stdArea
		FROM Histograms
		JOIN Pyroprints USING (pyroID)
		JOIN Regions USING (appliedRegion)
		WHERE pyroID = pyro AND position < pearsonDispLength;

	INSERT INTO ZScores (pyroID, position, zHeight, zWidth, zArea)
		SELECT pyroID, position,
				(pHeight - avgHeight)/stdHeight,
				(PeakWidth - avgWidth)/stdWidth,
				(PeakArea - avgArea)/stdArea
			FROM Histograms
			JOIN Pyroprints USING (pyroID)
			JOIN Regions USING (appliedRegion)
			WHERE pyroID = pyro AND position < pearsonDispLength;
	
END ;;
DELIMITER ;
