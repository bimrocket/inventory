/**
 * Aquestes dades estan hardcoded en l'aplicació,
 * un cop tinguin endpoint cal eliminar-los i recuperar-las 
 * mitjançant endpoint
 */
export interface TransactionType {
    id: number,
    description: string
}
export interface TransactionStatus {
    id: number,
    description: string
}
export const transTypes: Array<TransactionType> = [
    {
        id: 1,
        description: 'EDIT_USUARIS.TYPE_NEW_CC'
    },
    {
        id: 2,
        description: 'EDIT_USUARIS.TYPE_TICKET'
    }
];
export const transStatuses: Array<TransactionStatus> = [
    {
        id: 1,
        description: 'EDIT_USUARIS.STATUS_1'
    },
    {
        id: 2,
        description: 'EDIT_USUARIS.STATUS_2'
    },
    {
        id: 3,
        description: 'EDIT_USUARIS.STATUS_3'
    },
    {
        id: 4,
        description: 'EDIT_USUARIS.STATUS_4'
    },
    {
        id: 5,
        description: 'EDIT_USUARIS.STATUS_5'
    },
    {
        id: 6,
        description: 'EDIT_USUARIS.STATUS_6'
    },
    {
        id: 7,
        description: 'EDIT_USUARIS.STATUS_7'
    },
    {
        id: 10,
        description: 'EDIT_USUARIS.STATUS_10'
    },
    {
        id: 11,
        description: 'EDIT_USUARIS.STATUS_11'
    },
    {
        id: 12,
        description: 'EDIT_USUARIS.STATUS_12'
    },
    {
        id: 13,
        description: 'EDIT_USUARIS.STATUS_13'
    },
    {
        id: 14,
        description: 'EDIT_USUARIS.STATUS_14'
    },
    {
        id: 15,
        description: 'EDIT_USUARIS.STATUS_15'
    },
    {
        id: 16,
        description: 'EDIT_USUARIS.STATUS_16'
    },
    {
        id: 17,
        description: 'EDIT_USUARIS.STATUS_17'
    },
    {
        id: 18,
        description: 'EDIT_USUARIS.STATUS_18'
    },
    {
        id: 19,
        description: 'EDIT_USUARIS.STATUS_19'
    },
    {
        id: 20,
        description: 'EDIT_USUARIS.STATUS_20'
    },
    {
        id: 21,
        description: 'EDIT_USUARIS.STATUS_21'
    }
];