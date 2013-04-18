SELECT
	l.ID,
	l.Companion,
	v.BillID,
	v.LegalTitle,
	v.LongDescription
FROM 
	Legislation l LEFT OUTER JOIN LegislationVersion v 
		ON l.ID = v.LegislationID	
WHERE
	l.BillNumber = '1001' AND
	l.Biennium = '2011-12'
ORDER BY
	v.IntroducedDate DESC;