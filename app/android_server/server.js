import express from 'express';
import dotenv from 'dotenv';
import 'babel-polyfill';
import UserWithDb from './src/controller/User';
import Auth from './src/middleware/Auth';

dotenv.config();
const app = express()

app.use(express.json())

app.get('/', (req, res) => {
  return res.status(200).send({'message': 'YAY! Congratulations! Your first endpoint is working'});
});


app.post('/users', UserWithDb.create);
app.post('/users/login',UserWithDb.login);
app.put('/users/change/:login',Auth.verifyToken,UserWithDb.chagePass);
app.get('/users/:login',Auth.verifyToken,UserWithDb.getUser);
app.delete('/users/:login', Auth.verifyToken, UserWithDb.delete);

app.listen(80)
console.log('app running on port ', 80);