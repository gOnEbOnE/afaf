FROM node:20 AS build-stage

WORKDIR /app

COPY package*.json ./

ARG VITE_API_URL
# ARG VITE_BE2_API_URL

ENV VITE_API_URL=$VITE_API_URL
# ENV VITE_BE2_API_URL=$VITE_BE2_API_URL

RUN npm ci

COPY . .

RUN echo "VITE_API_URL=$VITE_API_URL" > .env.production

RUN npm run build

FROM nginx:alpine

RUN rm -rf /usr/share/nginx/html/*

COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]