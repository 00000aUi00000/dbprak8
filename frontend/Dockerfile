# development
FROM public.ecr.aws/docker/library/node:lts-alpine as development
WORKDIR /app
COPY package*.json ./
RUN npm install
EXPOSE 4200
CMD ["npm", "start"]

# build
FROM development as build
COPY . .
RUN npm run build

# production
FROM public.ecr.aws/docker/library/nginx:alpine as production
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /app/dist/frontend /usr/share/nginx/html
CMD ["nginx", "-g", "daemon off;"]
