import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 4139,
  login: 'G@CX\\/X8CT\\D2jJ',
};

export const sampleWithPartialData: IUser = {
  id: 7655,
  login: 'dD@lj\\>GtwcJ\\Ojfs',
};

export const sampleWithFullData: IUser = {
  id: 22293,
  login: 'Ic@94b\\jgrP0l',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
