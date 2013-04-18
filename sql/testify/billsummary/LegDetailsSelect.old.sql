SELECT
	l.ID,
	l.Companion,
	v.BillID,
	v.LegalTitle,
	v.LongDescription
FROM 
	Legislation l,
	LegislationVersion v
WHERE
	l.ID = v.LegislationID 
AND
	l.BillNumber = '1001' AND
	l.Biennium = '2011-12'
ORDER BY
	v.IntroducedDate DESC