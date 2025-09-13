-- Update existing caravanes to set approval_status to PENDING
-- This is needed because we added the approval_status field after caravanes were already created
UPDATE caravanes SET approval_status = 'PENDING' WHERE approval_status = '' OR approval_status IS NULL;

-- Optionally, you can approve all existing caravanes if you want them to be immediately available:
-- UPDATE caravanes SET approval_status = 'APPROVED' WHERE approval_status = 'PENDING';