import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: '714a6156-8c01-45e2-97ad-b41eec1320e9',
};

export const sampleWithPartialData: IAuthority = {
  name: 'ef838b7f-3de7-490d-a4be-8733eb7fd03d',
};

export const sampleWithFullData: IAuthority = {
  name: 'bf919363-5bbd-429a-a496-b6074c3e512a',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
