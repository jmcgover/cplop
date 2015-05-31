SELECT 
   p.appliedRegion, 
   p.pyroId, 
   p.isoID, 
   p.isErroneous, 
   i.hostId, 
   i.commonName 
   FROM 
      Pyroprints p, Histograms h, Isolates i 
   WHERE h.pyroID = p.pyroID 
     AND i.isoId = p.isoId 
   GROUP BY pyroid 
   ORDER BY commonName, hostId, isoId, pyroId, appliedRegion
;
