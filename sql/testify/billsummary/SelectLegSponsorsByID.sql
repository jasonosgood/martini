SELECT 
-- s.*, m.* 
	m.LastName
FROM 
	Sponsor s, Member m
WHERE 
	s.MemberID = m.ID
AND
	s.LegislationID = 3000 ORDER BY s.SponsorOrder ASC;